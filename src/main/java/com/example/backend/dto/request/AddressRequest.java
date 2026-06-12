package com.example.backend.dto.request;

import lombok.Data;

@Data
public class AddressRequest {
    private String addressLine;
    private Boolean isDefault;
}
