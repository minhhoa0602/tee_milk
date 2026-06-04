package com.example.backend.service;

import com.example.backend.dto.request.AddressRequest;
import com.example.backend.dto.response.AddressResponse;
import com.example.backend.entity.User;

import java.util.List;

public interface IAddressService {
    AddressResponse createAddress(User user, AddressRequest request);
    List<AddressResponse> getAddressesByUser(User user);
    AddressResponse setDefaultAddress(User user, Integer addressId);
}
