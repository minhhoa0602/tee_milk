package com.example.backend.service;

import com.example.backend.dto.request.AddressRequest;
import com.example.backend.dto.response.AddressResponse;
import com.example.backend.entity.Address;
import com.example.backend.entity.User;
import com.example.backend.mapper.AddressMapper;
import com.example.backend.repository.IAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService {

    private final IAddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressResponse createAddress(User user, AddressRequest addressRequest) {
// Logic xử lý: Nếu địa chỉ mới là mặc định -> Tắt mặc định của các địa chỉ cũ trước đó
        if (Boolean.TRUE.equals(addressRequest.getIsDefault())){
            List<Address> oldAddresss = addressRepository.findByUserId(user.getId());
            for (Address old : oldAddresss) {
                if (Boolean.TRUE.equals(old.getIsDefault())){
                    old.setIsDefault(false);
                    addressRepository.save(old);
                }
            }
        }
        Address address = new Address();
        address.setUser(user);
        address.setAddressLine(addressRequest.getAddressLine());
        address.setIsDefault(
                addressRequest.getIsDefault() != null
                ? addressRequest.getIsDefault()
                : false
        );
        Address savedAddress = addressRepository.save(address);
        return addressMapper.addressToAddressResponse(savedAddress);
    }

    @Override
    public List<AddressResponse> getAddressesByUser(User user) {
        List<Address> addresses = addressRepository.findByUserId(user.getId());
        return addressMapper.addresssToAddressResponses(addresses);
    }
}
