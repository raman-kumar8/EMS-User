package com.example.emsuser.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

    @Data
    @AllArgsConstructor
    public class ErrorDetails {

        private String message;
        private int status;
    }
