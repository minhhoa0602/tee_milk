package com.example.backend.service;

import com.example.backend.dto.request.UpdateProfileRequest;
import com.example.backend.dto.response.UserProfileResponse;
import com.example.backend.entity.Address;
import com.example.backend.entity.User;
import com.example.backend.repository.IAddressRepository;
import com.example.backend.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IAddressRepository addressRepository;

    @Override
    public UserProfileResponse getProfile(User user) {
        // Tìm xem user này có địa chỉ nào được đặt làm mặc định (is_default = true) chưa
        List<Address> addresses = addressRepository.findByUserId(user.getId());
        String defaultAddressLine = addresses.stream()
                .filter(addr -> Boolean.TRUE.equals(addr.getIsDefault()))
                .map(Address::getAddressLine)
                .findFirst()
                .orElse("Chưa thiết lập địa chỉ mặc định"); // Trả về chuỗi này nếu chưa có

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .avatarUrl(user.getAvatar())
                .defaultAddress(defaultAddressLine)
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(User user, UpdateProfileRequest request) {
        // Lấy lại User entity từ DB để đảm bảo dữ liệu mới nhất
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // Cập nhật thông tin mới từ màn hình chỉnh sửa của Mobile gửi lên
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            existingUser.setFullName(request.getFullName().trim());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            existingUser.setPhone(request.getPhoneNumber().trim());
        }
        if (request.getAvatarUrl() != null) {
            existingUser.setAvatar(request.getAvatarUrl());
        }

        User updatedUser = userRepository.save(existingUser);

        // Trả về profile mới sau khi sửa đổi thành công để Mobile cập nhật lại UI
        return getProfile(updatedUser);
    }
}