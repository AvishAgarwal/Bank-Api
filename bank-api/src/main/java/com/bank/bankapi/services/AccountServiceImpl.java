package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;
import com.bank.bankapi.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    Logger logger= LoggerFactory.getLogger(AccountServiceImpl.class);
    @Autowired
    AccountRepository accountRepository;

    @Override
    public Integer createAccount(Account account) throws BBadRequestException {
        logger.info("Creating account");
        int accountNumber = accountRepository.createAccount(account);
        if (account == null)
            throw new BBadRequestException("Unable to create account");
        return accountNumber;
    }

    @Override
    public Account getAccountByAccNo(int accountNumber) {
        return accountRepository.getAccountByAccNo(accountNumber);
    }

    @Override
    public boolean deleteAccount(int accountNumber) throws BNotFoundException {
        return accountRepository.deleteAccount(accountNumber);
    }
}
