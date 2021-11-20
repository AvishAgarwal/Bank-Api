package com.bank.bankapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BInternalServerErrorException extends RuntimeException{
    public BInternalServerErrorException(String message){super(message);}
}
