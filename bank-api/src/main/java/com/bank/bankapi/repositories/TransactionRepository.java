package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;

import java.util.List;

public interface TransactionRepository {
    Integer createTransaction(int from,int to,double amount,double from_balance,double to_balance)throws BAuthException;
    List<Transaction> getTransactions(String start,String stop, int accountNumber) ;
}
