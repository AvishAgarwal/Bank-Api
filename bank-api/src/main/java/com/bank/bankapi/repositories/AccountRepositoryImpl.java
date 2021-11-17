package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.exceptions.BAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository{

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public Account createAccount(Account account) throws BAuthException {
        return null;
    }
}
