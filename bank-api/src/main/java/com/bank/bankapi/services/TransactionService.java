package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;

public interface TransactionService {
    Integer createTransaction(Account from, Account to, double amount)throws BAuthException;
    boolean getTransaction(String start ,String stop, int accountNumber) throws BAuthException;
}
