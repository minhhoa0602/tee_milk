package com.example.backend.service;

import com.example.backend.dto.request.AddressRequest;
import com.example.backend.dto.response.AddressResponse;
import com.example.backend.entity.Address;
import com.example.backend.entity.User;
import com.example.backend.mapper.AddressMapper;
import com.example.backend.repository.IAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(User user, Integer addressId) {
        // 1. Tìm địa chỉ mà khách muốn chọn làm mặc định
        Address targetAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new com.example.backend.exception.ApplicationException("Không tìm thấy địa chỉ!", 404, 404));

        // Bảo mật: Kiểm tra xem địa chỉ này có đúng là của User đang đăng nhập không
        if (!targetAddress.getUser().getId().equals(user.getId())) {
            throw new com.example.backend.exception.ApplicationException("Bạn không có quyền thao tác trên địa chỉ này!", 403, 403);
        }

        // 2. Lấy tất cả địa chỉ của user này và tắt trạng thái mặc định của chúng đi
        List<Address> allAddresses = addressRepository.findByUserId(user.getId());
        for (Address addr : allAddresses) {
            if (Boolean.TRUE.equals(addr.getIsDefault())) {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            }
        }

        // 3. Bật trạng thái mặc định cho địa chỉ được chọn
        targetAddress.setIsDefault(true);
        Address updatedAddress = addressRepository.save(targetAddress);

        return AddressResponse.builder()
                .id(updatedAddress.getId())
                .addressLine(updatedAddress.getAddressLine())
                .isDefault(updatedAddress.getIsDefault())
                .build();
    }
}
