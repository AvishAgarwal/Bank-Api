package com.bank.bankapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class BAuthException extends RuntimeException {
    public BAuthException(String message){
        super(message);
    }
}
