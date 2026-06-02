package com.example.backend.mapper;

import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User fromRegister(RegisterRequest request);
}
