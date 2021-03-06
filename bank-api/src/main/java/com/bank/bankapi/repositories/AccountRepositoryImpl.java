package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.User;
import com.bank.bankapi.exceptions.BAuthException;
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

@Repository
public class AccountRepositoryImpl implements AccountRepository {
    Logger logger= LoggerFactory.getLogger(AccountRepositoryImpl.class);
    private static final String CREATEACCOUNT = "insert into bt_accounts(account_number,user_id,type,current_balance,is_deleted,created_at,last_updated_at,last_interest_added) \n" +
            "values(NEXTVAL('bt_accounts_seq'),?,?,?,false,now(),now(),now());";
    private static final String GETACCOUNTBYNUMBER = "Select * from bt_accounts where account_number=? and is_deleted=false";
    private static final String DELETEACCOUNT = "update bt_accounts set is_deleted= true ,  last_updated_at=now() where account_number=?";
    private static final String UPDATEBALANCE = "update bt_accounts set current_balance=? ,  last_updated_at=now() where account_number=?";
    private static final String UPDATEINTEREST = "update bt_accounts set current_balance=? ,  last_updated_at=now(),last_interest_added=now() where account_number=?";
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer createAccount(Account account) throws BAuthException {
        try {
            logger.info("Running Create account query");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connections -> {
                PreparedStatement preparedStatement = connections.prepareStatement(CREATEACCOUNT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, account.getUser_id());
                preparedStatement.setString(2, String.valueOf(account.getType()));
                preparedStatement.setDouble(3, account.getCurrent_balance());

                return preparedStatement;
            }, keyHolder);
            return (Integer) keyHolder.getKeys().get("account_number");
        } catch (Exception e) {
            throw new BAuthException("Unable to create Employee, invalid data");
        }
    }

    @Override
    public Account getAccountByAccNo(int accountNumber)throws BNotFoundException {
        logger.info("Running qeury for getting account");
        Account account;
        try
        {
            account = jdbcTemplate.queryForObject(GETACCOUNTBYNUMBER, userRowMapper, new Object[]{accountNumber});
        }catch (Exception e){
            throw new BNotFoundException("Unable to find account with given number");
        }
        return account;
    }

    @Override
    public boolean deleteAccount(int account_number) throws BAuthException {
        try {
            logger.info("Running query to delete Account");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connections -> {
                PreparedStatement preparedStatement = connections.prepareStatement(DELETEACCOUNT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, account_number);
                return preparedStatement;
            }, keyHolder);
            return (boolean) keyHolder.getKeys().get("is_deleted");
        } catch (Exception e) {
            throw new BAuthException("Unable to delete user, invalid data");
        }
    }

    @Override
    public boolean updateBalance(int account_number, double balance) {
        try {
            logger.info("Running query to update balance");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connections -> {
                PreparedStatement preparedStatement = connections.prepareStatement(UPDATEBALANCE, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setDouble(1, balance);
                preparedStatement.setInt(2, account_number);
                return preparedStatement;
            }, keyHolder);

            return keyHolder.getKeys().size() > 0;
        } catch (Exception e) {
            throw new BAuthException("Unable to delete user, invalid data");
        }
    }

    @Override
    public boolean updateInterest(int account_number, double balance) {
        try {
            logger.info("Running query to update interest");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connections -> {
                PreparedStatement preparedStatement = connections.prepareStatement(UPDATEINTEREST, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setDouble(1, balance);
                preparedStatement.setInt(2, account_number);
                return preparedStatement;
            }, keyHolder);
            return keyHolder.getKeys().size() > 0;
        } catch (Exception e) {
            throw new BAuthException("Unable to delete user, invalid data");
        }
    }

    private RowMapper<Account> userRowMapper = ((rs, rowNum) -> {
        return new Account(rs.getInt("account_number"),
                rs.getInt("user_id"),
                Account.Type.valueOf(rs.getString("type")),
                rs.getDouble("current_balance"),
                rs.getBoolean("is_deleted"),
                rs.getString("created_at"),
                rs.getString("last_updated_at"),
                rs.getString("last_interest_added"));
    });
}
