package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Integer id;
    private String addressLine;
    private Boolean isDefault;
}
