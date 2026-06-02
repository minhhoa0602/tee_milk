package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.config.CustomUserDetails;
import com.example.backend.dto.request.AddressRequest;
import com.example.backend.dto.response.AddressResponse;
import com.example.backend.service.AddressService;
import com.example.backend.service.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final IAddressService addressService;

    @PostMapping
    public ResponseEntity<BaseResponse> createAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AddressRequest request) {

        AddressResponse data = addressService.createAddress(userDetails.getUser(), request);
        return ResponseEntity.ok(new BaseResponse(200, data, "Thêm địa chỉ mới thành công"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getMyAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<AddressResponse> data = addressService.getAddressesByUser(userDetails.getUser());
        return ResponseEntity.ok(new BaseResponse(200, data, "Lấy danh sách địa chỉ thành công"));
    }
}
