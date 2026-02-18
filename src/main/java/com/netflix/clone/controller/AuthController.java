package com.netflix.clone.controller;


import com.netflix.clone.dto.request.*;
import com.netflix.clone.dto.response.EmailValidationResponse;
import com.netflix.clone.dto.response.LoginResposne;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.service.AuthService;
import jakarta.validation.Valid;

import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody UserRequest userRequest){
        return ResponseEntity.ok(authService.signup(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResposne> login (@Valid @RequestBody LoginRequest loginRequest){

        LoginResposne resposne = authService.login(loginRequest.getEmail(),loginRequest.getPassword());
        return ResponseEntity.ok(resposne);
    }

    @PostMapping("/validate-email")
    public ResponseEntity<EmailValidationResponse> validationEmail( @RequestParam String email ){
        return ResponseEntity.ok(authService.validateEmail(email));
    }


    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail( @RequestParam String token ){
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(authService.resendVerification(emailRequest.getEmail()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(authService.forgotPassword(emailRequest.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPassword resetPassword){
        return ResponseEntity.ok(authService.resetPassword(resetPassword.getToken(),resetPassword.getNewPassword()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication , @Valid @RequestBody ChangePasswordRequest changePasswordRequest ){
        String email= authentication.getName();

        return ResponseEntity.ok(authService.changePassword(email, changePasswordRequest.getCurrentPassword(),changePasswordRequest.getNewPassword()));
    }

    @GetMapping("/current-user")
    public ResponseEntity<LoginResposne> currentUser(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(authService.currentUser(email));
    }


}
