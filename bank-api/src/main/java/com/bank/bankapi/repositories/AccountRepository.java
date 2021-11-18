package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.exceptions.BAuthException;

public interface AccountRepository {
    Integer createAccount(Account account) throws BAuthException;

    Account getAccountByAccNo(int accountnumber);

    boolean deleteAccount(int account_number) throws BAuthException;

    boolean updateBalance(int account_number,double balance);
}
