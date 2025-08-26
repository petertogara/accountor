package com.accountor.prh.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ExternalApiException extends RuntimeException {

    private final String errorCode;
    private final String details;

    public ExternalApiException(String message, String errorCode, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }


    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.details = null;
    }

}