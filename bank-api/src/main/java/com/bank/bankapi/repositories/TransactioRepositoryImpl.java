package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class TransactioRepositoryImpl implements TransactionRepository{
    private static final String CREATETRANSACTION="insert into bt_transactions(transaction_id,from_id,to_id,amount,from_balance,to_balance,created_at)\n" +
            "values(NEXTVAL('bt_transactions_seq'),?,?,?,?,?,now())";
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public Integer createTransaction(int from, int to, double amount, double from_balance, double to_balance) throws BAuthException {
        try{

            KeyHolder keyHolder= new GeneratedKeyHolder();
            jdbcTemplate.update(connections->{
                PreparedStatement preparedStatement= connections.prepareStatement(CREATETRANSACTION, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1,from);
                preparedStatement.setInt(2,to);
                preparedStatement.setDouble(3,amount);
                preparedStatement.setDouble(4,from_balance);
                preparedStatement.setDouble(5,to_balance);

                return preparedStatement;
            },keyHolder);
            return (Integer) keyHolder.getKeys().get("transaction_id");
        }
        catch (Exception e)
        {
            throw new BAuthException("Unable to create transaction, invalid data");
        }
    }
}
