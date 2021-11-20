package com.bank.bankapi.services;

import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;

public interface EmployeeService {
    Employee validateEmployee(String phone, String password,boolean status) throws BAuthException;

    Employee registerEmployee(String firstName, String lastName, String password, Employee.Role role, String phone) throws BBadRequestException;

    boolean deleteEmployee(String phone) throws BNotFoundException;

    Employee logout(Employee employee) throws BNotFoundException;
}
