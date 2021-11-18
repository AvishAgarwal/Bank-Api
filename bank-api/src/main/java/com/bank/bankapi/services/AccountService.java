package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.exceptions.BAuthException;

public interface AccountService {
Integer createAccount(Account account) throws BAuthException;
Account getAccountByAccNo(Account account);
}
