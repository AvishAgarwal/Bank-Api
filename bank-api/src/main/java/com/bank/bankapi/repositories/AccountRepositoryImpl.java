package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.exceptions.BAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class AccountRepositoryImpl implements AccountRepository{
    private static final String CREATEACCOUNT="insert into bt_accounts(account_number,user_id,type,current_balance,is_deleted,created_at,last_updated_at) \n" +
            "values(NEXTVAL('bt_accounts_seq'),?,?,?,false,now(),now());";
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public Integer createAccount(Account account) throws BAuthException {
        try{

            KeyHolder keyHolder= new GeneratedKeyHolder();
            jdbcTemplate.update(connections->{
                PreparedStatement preparedStatement= connections.prepareStatement(CREATEACCOUNT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1,account.getUser_id());
                preparedStatement.setString(2,String.valueOf(account.getType()));
                preparedStatement.setDouble(3,account.getCurrent_balance());

                return preparedStatement;
            },keyHolder);
            return (Integer) keyHolder.getKeys().get("account_number");
        }
        catch (Exception e)
        {
            throw new BAuthException("Unable to create Employee, invalid data");
        }
    }

    @Override
    public Account getAccountByAccNo(Account account) {
        return null;
    }
}
