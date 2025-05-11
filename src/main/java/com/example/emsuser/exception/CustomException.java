package com.example.emsuser.exception;

import lombok.Data;


public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
}
