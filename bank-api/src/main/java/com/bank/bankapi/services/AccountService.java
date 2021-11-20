package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;

public interface AccountService {
    Integer createAccount(Account account) throws BBadRequestException;

    Account getAccountByAccNo(int accountNumber);

    boolean deleteAccount(int accountNumber) throws BNotFoundException;
}
