package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.repositories.AccountRepository;
import com.bank.bankapi.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;


    @Override
    public Integer createTransaction(Account from, Account to, double amount) throws BAuthException {
        from.setCurrent_balance(from.getCurrent_balance()-amount);
        to.setCurrent_balance(to.getCurrent_balance()+amount);

        int transactionId =transactionRepository.createTransaction(from.getAccount_number(),to.getAccount_number(),amount,
                from.getCurrent_balance(), to.getCurrent_balance());
        boolean flag1= accountRepository.updateBalance(from.getAccount_number(), from.getCurrent_balance());

        boolean flag2=accountRepository.updateBalance(to.getAccount_number(), to.getCurrent_balance());
        if(!flag1 && !flag2)
            throw new BAuthException("Unable to update balance");

        return transactionId;
    }

    @Override
    public boolean getTransaction(String start, String stop, int accountNumber) throws BAuthException {
        List<Transaction> list= transactionRepository.getTransactions(start,stop,accountNumber);
        return true;
    }
}
