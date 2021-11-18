package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.exceptions.BAuthException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository{

    private static final String CREATE_SQL ="insert into bt_employees(user_id,first_name,last_name,phone,password,role,is_deleted,created_at,last_updated_at,is_active)" +
            " values(NEXTVAL('bt_employees_seq'), ?, ?, ?, ?,?,false,now(),now(),false)";
    private static final String COUNT_EMPLOYEE_WITH_PHONE = "select count(*) from bt_employees where phone= ? and is_deleted=false";

    private static final String GET_EMPLOYEE_BY_ID ="select * from bt_employees where user_id= ? and is_deleted=false";
    private static final String GET_EMPLOYEE_BY_PHONE ="select * from bt_employees where phone=? and is_deleted=false";
    private static final String DELETE_EMPLOYEE_WITH_PHONE="update bt_employees set is_deleted= true ,  last_updated_at=now() where phone = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer createEmployee(String firstName, String lastName, String password, Employee.Role role, String phone) throws BAuthException {

        try{
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
            KeyHolder keyHolder= new GeneratedKeyHolder();
            jdbcTemplate.update(connections->{
                PreparedStatement preparedStatement= connections.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1,firstName);
                preparedStatement.setString(2,lastName);
                preparedStatement.setString(3,phone);
                preparedStatement.setString(4,hashedPassword);
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
    public Employee findEmployeeByIdandPassword(String phone, String password) throws BAuthException {
        try{
            Employee employee= jdbcTemplate.queryForObject(GET_EMPLOYEE_BY_PHONE,userRowMapper,new Object[]{phone});
            if(employee.getRole()== Employee.Role.ADMIN){
                if(!password.equals(employee.getPassword()))
                    throw new BAuthException("Incorrect phone/password");
            }
            else{
                if (!BCrypt.checkpw(password, employee.getPassword()))
                    throw new BAuthException("Incorrect phone/password");
            }
            return employee;
        }
        catch (EmptyResultDataAccessException e)
        {
            throw new BAuthException("Incorrect phone/password");
        }
    }

    @Override
    public Integer checkEmployeePhone(String phone) {
        return jdbcTemplate.queryForObject(COUNT_EMPLOYEE_WITH_PHONE,Integer.class,new Object[]{phone});
    }

    @Override
    public Employee findEmployeeById(int user_id) {
        return jdbcTemplate.queryForObject(GET_EMPLOYEE_BY_ID,userRowMapper,new Object[]{user_id});
    }

    @Override
    public boolean deleteEmployeeByPhone(String phone) throws BAuthException {
        try{
            KeyHolder keyHolder= new GeneratedKeyHolder();
            jdbcTemplate.update(connections->{
                PreparedStatement preparedStatement= connections.prepareStatement(DELETE_EMPLOYEE_WITH_PHONE, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1,phone);

                return preparedStatement;
            },keyHolder);
            return (boolean) keyHolder.getKeys().get("is_deleted");
        }
        catch (Exception e)
        {
            throw new BAuthException("Unable to delete Employee, invalid data");
        }
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
                rs.getString("last_updated_at"),
                rs.getBoolean("is_active"));
    });
}
