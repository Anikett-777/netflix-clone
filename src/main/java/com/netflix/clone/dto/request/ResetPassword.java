package com.netflix.clone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPassword {


    @NotBlank
    private String token;

    @NotBlank
    @Size(min=6, message = "New Password Must be at least 6 character Long")
    private String newPassword;

}
