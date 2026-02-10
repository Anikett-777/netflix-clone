package com.netflix.clone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResposne {
    private String token;
    private String email;
    private String fullName;
    private String role;
}
