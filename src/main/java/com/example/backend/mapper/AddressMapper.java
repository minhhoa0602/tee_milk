package com.example.backend.mapper;

import com.example.backend.dto.response.AddressResponse;
import com.example.backend.entity.Address;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse addressToAddressResponse(Address address);

    List<AddressResponse> addresssToAddressResponses(List<Address> addressList);
}
