package com.netflix.clone.serviceImpl;

import com.netflix.clone.dao.UserRepository;
import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.EmailValidationResponse;
import com.netflix.clone.dto.response.LoginResposne;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.entity.User;
import com.netflix.clone.enums.Role;
import com.netflix.clone.exceptions.*;
import com.netflix.clone.security.JwtUtil;
import com.netflix.clone.service.AuthService;
import com.netflix.clone.service.EmailService;
import com.netflix.clone.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ServiceUtils serviceUtils;

    @Override
    public MessageResponse signup(UserRequest userRequest) {
        if(userRepository.existsByEmail( userRequest.getEmail())){
            throw new EmailAlreadyExistsException("Email Already Exist.");
        }
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.USER);
        user.setActive(true);
        user.setEmailVerified(false);
        String verificationToken= UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);

        emailService.sendVerificationEmail(userRequest.getEmail(),verificationToken);
        



        return new MessageResponse("Registration Successfully done. Please Check Your Email for verify Your Account.");
    }

    @Override
    public LoginResposne login(String email, String password) {

        User user = userRepository
                .findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(()-> new BadCredentialsException("Invalid Email Or Password"));

        if(!user.isActive()){
            throw new AccountDeactivatedException(
                    "Your Account Has Been Deactivated.Please contact for support for assistance");
        }

        if(!user.isEmailVerified()){
            throw  new EmailNotVerifiedException(
                    "Please Verify Your email Address before logging in.Check your inbox for the " +
                            "verification link.");
        }

        final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResposne(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Override
    public EmailValidationResponse validateEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);


        return new EmailValidationResponse(exists,!exists);
    }

    @Override
    public MessageResponse verifyEmail(String token) {

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(()-> new InvalidTokenException("Invalid or expired Verification Token"));
        if(user.getVerificationTokenExpiry()== null || user.getVerificationTokenExpiry().isBefore(Instant.now())){
            throw  new InvalidTokenException("Verification Link Has Expired. Please request new one");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationToken(null);
        userRepository.save(user);
        return new MessageResponse("Email Verified Successfully.You can now Login");
    }

    @Override
    public MessageResponse resendVerification(String email) {

        User user = serviceUtils.getUserByEmailOrThrow(email);
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);
        emailService.sendVerificationEmail(email,verificationToken);


        return new MessageResponse("Verification email resent successfully! Please Check Your Inbox");
    }

    @Override
    public MessageResponse forgotPassword(String email) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(3600));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(email,resetToken);

        return  new MessageResponse(" Password Reset Email send successfully! Please Check Your Inbox");
    }

    @Override
    public MessageResponse resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token).orElseThrow(()->new InvalidTokenException("Invalid Or Expired reset Token "));

        if(user.getPasswordResetTokenExpiry()== null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())){
            throw  new InvalidTokenException("Reset Token Has Expired ");

        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Password Reset Successfully. You can now login With new Password");
    }

    @Override
    public MessageResponse changePassword(String email, String currentPassword, String newPassword) {
        User user = serviceUtils.getUserByEmailOrThrow(email);

        if(!passwordEncoder.matches(currentPassword,user.getPassword())){
            throw  new InvalidCredentialsException("Current Password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new MessageResponse("Password Changed Successfully");
    }

    @Override
    public LoginResposne currentUser(String email) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        return  new LoginResposne(null,user.getEmail(), user.getFullName(),user.getRole().name() );

    }

}
