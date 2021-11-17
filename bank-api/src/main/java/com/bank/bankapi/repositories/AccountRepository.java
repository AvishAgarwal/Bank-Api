package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.exceptions.BAuthException;

public interface AccountRepository {
    Account createAccount(Account account) throws BAuthException;
}
