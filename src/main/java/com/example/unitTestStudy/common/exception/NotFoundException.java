package com.example.unitTestStudy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotFoundException extends RuntimeException{
    private ExceptionCode code;
    public NotFoundException(String message) {
        super(message);
    }
}
