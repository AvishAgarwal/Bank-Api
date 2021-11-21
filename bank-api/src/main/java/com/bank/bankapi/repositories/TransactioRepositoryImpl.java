package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class TransactioRepositoryImpl implements TransactionRepository {
    Logger logger= LoggerFactory.getLogger(TransactioRepositoryImpl.class);
    private static final String CREATETRANSACTION = "insert into bt_transactions(transaction_id,from_id,to_id,amount,from_balance,to_balance,created_at)\n" +
            "values(NEXTVAL('bt_transactions_seq'),?,?,?,?,?,now())";
    private static final String GETTRANSACTION = "select * from bt_transactions where (from_id=? or to_id=?) " +
            "and created_at between ?::timestamp and ?::timestamp order by created_at desc";
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer createTransaction(int from, int to, double amount, double from_balance, double to_balance) throws BBadRequestException {
        try {
            logger.info("Running Query to create transaction");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connections -> {
                PreparedStatement preparedStatement = connections.prepareStatement(CREATETRANSACTION, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, from);
                preparedStatement.setInt(2, to);
                preparedStatement.setDouble(3, amount);
                preparedStatement.setDouble(4, from_balance);
                preparedStatement.setDouble(5, to_balance);

                return preparedStatement;
            }, keyHolder);
            return (Integer) keyHolder.getKeys().get("transaction_id");
        } catch (Exception e) {
            throw new BBadRequestException("Unable to create transaction, invalid data");
        }
    }

    @Override
    public List<Transaction> getTransactions(String start, String stop, int accountNumber)throws BNotFoundException {
        logger.info("Running query to get transaction between {} to {}",start,stop);
        List<Transaction> list=null;
        try
        {
            jdbcTemplate.query(GETTRANSACTION, userRowMapper, new Object[]{accountNumber, accountNumber, start, stop});
        }catch (Exception e){
            throw new BNotFoundException("Data not found between the range");
        }
        return list;
    }

    private RowMapper<Transaction> userRowMapper = ((rs, rowNum) -> {
        return new Transaction(rs.getInt("transaction_id"),
                rs.getInt("from_id"),
                rs.getInt("to_id"),
                rs.getDouble("amount"),
                rs.getDouble("from_balance"),
                rs.getDouble("to_balance"),
                rs.getString("created_at"));
    });
}
