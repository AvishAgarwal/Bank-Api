package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService{

    @Autowired
    AccountRepository accountRepository;
    @Override
    public Integer createAccount(Account account) throws BAuthException {
        int accountNumber= accountRepository.createAccount(account);
        if(account==null)
            throw new BAuthException("Unable to create account");
        return accountNumber;
    }

    @Override
    public Account getAccountByAccNo(int accountNumber) {
        return accountRepository.getAccountByAccNo(accountNumber);
    }

    @Override
    public boolean deleteAccount(int accountNumber) throws BAuthException {
        return accountRepository.deleteAccount(accountNumber);
    }
}
