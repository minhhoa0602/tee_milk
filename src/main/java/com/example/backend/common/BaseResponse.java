package com.example.backend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private Integer code;
    private T data;
    private String message;

    public BaseResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }
}
