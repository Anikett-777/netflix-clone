package com.netflix.clone.service;

import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.EmailValidationResponse;
import com.netflix.clone.dto.response.LoginResposne;
import com.netflix.clone.dto.response.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface AuthService {
    MessageResponse signup(@Valid UserRequest userRequest);

    LoginResposne login(@NotBlank(message = "email is required") @Email(message = "invalid email format") String email, @NotBlank(message = "password is Required") String password);


    EmailValidationResponse validateEmail(String email);

    MessageResponse verifyEmail(String token);

    MessageResponse resendVerification(@NotBlank(message = "Email is Required") @Email(message = "Invalid Email Format") String email);

    MessageResponse forgotPassword(@NotBlank(message = "Email is Required") @Email(message = "Invalid Email Format") String email);

    MessageResponse resetPassword(@NotBlank String token, @NotBlank @Size(min=6, message = "New Password Must be at least 6 character Long") String newPassword);


    MessageResponse changePassword(String email, @NotBlank(message = "Current Password is Required") String currentPassword, @NotBlank(message = "New Password is Required") String newPassword);

    LoginResposne currentUser(String email);
}

