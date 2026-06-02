package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class RegisterRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String fullName;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;

}
