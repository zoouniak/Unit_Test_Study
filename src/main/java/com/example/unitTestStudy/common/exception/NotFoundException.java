package com.example.unitTestStudy.common.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotFoundException extends RuntimeException{
    private ExceptionCode code;
    public NotFoundException(String message) {
        super(message);
    }
}
