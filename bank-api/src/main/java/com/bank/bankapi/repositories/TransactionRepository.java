package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;

public interface TransactionRepository {
    Integer createTransaction(int from,int to,double amount,double from_balance,double to_balance)throws BAuthException;
}
