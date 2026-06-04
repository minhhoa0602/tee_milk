package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String defaultAddress;
}
