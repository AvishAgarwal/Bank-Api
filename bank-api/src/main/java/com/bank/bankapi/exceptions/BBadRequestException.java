package com.bank.bankapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BBadRequestException extends RuntimeException{
    public BBadRequestException(String message){super(message);}
}
