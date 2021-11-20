package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;

public interface EmployeeRepository {
    Integer createEmployee(String firstName, String lastName, String password, Employee.Role role, String phone) throws BBadRequestException;

    Employee findEmployeeByIdandPassword(String phone,String password) throws BNotFoundException;

    Integer checkEmployeePhone(String phone);

    Employee findEmployeeById(int user_id)throws BNotFoundException;

    boolean deleteEmployeeByPhone(String phone) throws BBadRequestException;
    boolean updateEmployeeIsActive(Employee employee)throws BBadRequestException;
}
