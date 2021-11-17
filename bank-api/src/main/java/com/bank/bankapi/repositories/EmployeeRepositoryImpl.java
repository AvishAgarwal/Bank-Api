package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.exceptions.BAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository{

    private static final String createSql="insert into bt_employees(user_id,first_name,last_name,phone,password,role,is_deleted,created_at,last_updated_at)" +
            " values(NEXTVAL('bt_employees_seq'), ?, ?, ?, ?,?,false,now(),now())";
    private static final String countEmployeeSql = "select count(*) from bt_employees where phone= ?";

    private static final String getEmployeeByIdSql ="select * from bt_employees where user_id= ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer createEmployee(String firstName, String lastName, String password, Employee.Role role, String phone) throws BAuthException {

        try{
            KeyHolder keyHolder= new GeneratedKeyHolder();
            jdbcTemplate.update(connections->{
                PreparedStatement preparedStatement= connections.prepareStatement(createSql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1,firstName);
                preparedStatement.setString(2,lastName);
                preparedStatement.setString(3,phone);
                preparedStatement.setString(4,password);
                preparedStatement.setString(5,String.valueOf(role));
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
    public Employee findEmployeeByIdandPassword(int user_id, String password) throws BAuthException {
        return null;
    }

    @Override
    public Integer checkEmployeePhone(String phone) {
        return jdbcTemplate.queryForObject(countEmployeeSql,Integer.class,new Object[]{phone});
    }

    @Override
    public Employee findEmployeeById(int user_id) {
        return jdbcTemplate.queryForObject(getEmployeeByIdSql,userRowMapper,new Object[]{user_id});
    }

    private RowMapper<Employee> userRowMapper = ((rs, rowNum) -> {
        return new Employee(rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getString("password"),
                Employee.Role.valueOf(rs.getString("role")),
                rs.getBoolean("is_deleted"),
                rs.getString("created_at"),
                rs.getString("last_updated_at"));
    });
}
