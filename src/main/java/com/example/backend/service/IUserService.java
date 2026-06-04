package com.example.backend.service;

import com.example.backend.dto.request.UpdateProfileRequest;
import com.example.backend.dto.response.UserProfileResponse;
import com.example.backend.entity.User;

public interface IUserService {
    UserProfileResponse getProfile(User user);
    UserProfileResponse updateProfile(User user, UpdateProfileRequest updateProfileRequest);
}
