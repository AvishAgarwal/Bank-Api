package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;

import java.util.List;

public interface TransactionRepository {
    Integer createTransaction(int from, int to, double amount, double from_balance, double to_balance) throws BBadRequestException;

    List<Transaction> getTransactions(String start, String stop, int accountNumber);
}
