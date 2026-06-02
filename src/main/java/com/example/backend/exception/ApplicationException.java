package com.example.backend.exception;

import lombok.Data;

@Data
public class ApplicationException extends RuntimeException {
    private  int code;
    private int http;
    public ApplicationException(String message, int code, int http) {
        super(message);
        this.code = code;
        this.http = http;
    }
}
