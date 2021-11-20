package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;
import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.text.ParseException;

public interface TransactionService {
    Integer createTransaction(Account from, Account to, double amount) throws BBadRequestException;

    boolean getTransaction(String start, String stop, int accountNumber) throws BNotFoundException, FileNotFoundException, DocumentException;

    Integer addInterest(Account from, Account to) throws BNotFoundException, ParseException;
}
