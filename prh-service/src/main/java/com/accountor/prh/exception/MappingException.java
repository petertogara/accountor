package com.accountor.prh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MappingException extends RuntimeException {
    public MappingException(String message) {
        super(message);
    }
}
