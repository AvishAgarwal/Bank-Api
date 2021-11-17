package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.exceptions.BAuthException;

public interface AccountService {
Account createAccount(Account account) throws BAuthException;
}
