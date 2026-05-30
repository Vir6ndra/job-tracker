package com.user_service.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;
}