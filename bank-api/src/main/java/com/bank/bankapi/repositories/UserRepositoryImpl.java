package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.domain.User;
import com.bank.bankapi.exceptions.BAuthException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private static final String CREATEUSERSQL="insert into bt_users(user_id,first_name,last_name,phone,created_by,password,current_account_number,saving_account_number,loan_account_number,salary_account_number,kyc_status,adhaar_number,is_deleted,created_at,last_updated_at) \n" +
            "values(NEXTVAL('bt_users_seq'), ?, ?, ?, ?,?,?,?,?,?,?,?,false,now(),now())";
    private static final String CHECKUSERBYPHONE="select count(*) from bt_users where phone= ? and is_deleted=false";
    private static final String GETUSERBYID="select * from bt_users where user_id= ? and is_deleted=false";
    private static final String UPDATEKYC="update bt_users set kyc_status= ? ,adhaar_number=?  ,last_updated_at=now() where phone = ? and is_deleted=false";
    private static final String UPDATEACCOUNTS="update bt_users set current_account_number = ?, saving_account_number= ?,loan_account_number =?, salary_account_number=?,last_updated_at=now() where user_id=? and is_deleted=false";
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public Integer createUser(User user) throws BAuthException {
        try{
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
            KeyHolder keyHolder= new GeneratedKeyHolder();
            jdbcTemplate.update(connections->{
                PreparedStatement preparedStatement= connections.prepareStatement(CREATEUSERSQL, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1,user.getFirst_name());
                preparedStatement.setString(2,user.getLast_name());
                preparedStatement.setString(3,user.getPhone());
                preparedStatement.setInt(4,user.getCreated_by());
                preparedStatement.setString(5,hashedPassword);
                preparedStatement.setInt(6,user.getCurrent_account_number());
                preparedStatement.setInt(7,user.getSaving_account_number());
                preparedStatement.setInt(8,user.getLoan_account_number());
                preparedStatement.setInt(9,user.getSalary_account_number());
                preparedStatement.setString(10,String.valueOf(user.getKyc_status()));
                preparedStatement.setString(11,user.getAdhaar_number());
                return preparedStatement;
            },keyHolder);
            return (Integer) keyHolder.getKeys().get("user_id");
        }
        catch (Exception e)
        {
            throw new BAuthException("Unable to create Employee, invalid data");
        }
    }

    @Override
    public Integer checkUserPhone(String phone) throws BAuthException {
        return jdbcTemplate.queryForObject(CHECKUSERBYPHONE,Integer.class,new Object[]{phone});
    }

    @Override
    public User findUserById(int user_id) {
        return jdbcTemplate.queryForObject(GETUSERBYID,userRowMapper,new Object[]{user_id});
    }

    @Override
    public boolean updateKyc(String phone, String adhaar, User.Status status) throws BAuthException {
        try{
            KeyHolder keyHolder= new GeneratedKeyHolder();
            jdbcTemplate.update(connections->{
                PreparedStatement preparedStatement= connections.prepareStatement(UPDATEKYC, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1,String.valueOf(status));
                preparedStatement.setString(2,adhaar);
                preparedStatement.setString(3,phone);

                return preparedStatement;
            },keyHolder);
            return  (keyHolder.getKeys().get("kyc_status")).equals(String.valueOf(status));
        }
        catch (Exception e)
        {
            throw new BAuthException("Unable to Update User, invalid data");
        }
    }

    @Override
    public boolean updateAccounts(User user) {
        try{
            KeyHolder keyHolder= new GeneratedKeyHolder();
            jdbcTemplate.update(connections->{
                PreparedStatement preparedStatement= connections.prepareStatement(UPDATEACCOUNTS, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1,user.getCurrent_account_number());
                preparedStatement.setInt(2,user.getSaving_account_number());
                preparedStatement.setInt(3,user.getLoan_account_number());
                preparedStatement.setInt(4,user.getSalary_account_number());
                preparedStatement.setInt(5,user.getUser_id());

                return preparedStatement;
            },keyHolder);

            return  keyHolder.getKeys().size()!= 0;
        }
        catch (Exception e)
        {
            throw new BAuthException("Unable to update account, invalid data");
        }
    }

    private RowMapper<User> userRowMapper = ((rs, rowNum) -> {
        return new User(rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getInt("created_by"),
                rs.getString("password"),
                rs.getInt("current_account_number"),
                rs.getInt("saving_account_number"),
                rs.getInt("loan_account_number"),
                rs.getInt("salary_account_number"),
                User.Status.valueOf(rs.getString("kyc_status")),
                rs.getString("adhaar_number"),
                rs.getBoolean("is_deleted"),
                rs.getString("created_at"),
                rs.getString("last_updated_at"));
    });
}
