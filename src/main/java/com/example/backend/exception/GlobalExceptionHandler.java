package com.example.backend.exception;

import com.example.backend.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //bat loi do he hong
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse> handleRuntimeException(RuntimeException e) {
        log.error("error",e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(500,null,"sysem erroe"));
    }
    //bat loi @validate
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            String error = fieldError.getField() + ": " + fieldError.getDefaultMessage();
            errors.add(error);
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(400,null,errors.toString()));
    }
    //bat loi do phan quyen
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse> handleAuthenticationException(AuthenticationException e) {
        log.error("error",e);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new BaseResponse<>(403,null,"user not authorized"));
    }
    //bat loi do tu tao
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleRuntimeException(ApplicationException e) {
        return ResponseEntity
                .status(e.getHttp())
                .body(new BaseResponse<>(e.getCode(),null,e.getMessage()));
    }

}
