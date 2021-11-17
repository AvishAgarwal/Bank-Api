package com.bank.bankapi.services;

import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    EmployeeRepository employeeRepository;
    @Override
    public Employee validateEmployee(String user_id, String password) throws BAuthException {
        return null;
    }

    @Override
    public Employee registerEmployee( String firstName, String lastName, String password, Employee.Role role, String phone) throws BAuthException {
        if(phone.length()!=10)
            throw new BAuthException("Invalid Phone Number");
        Integer count = employeeRepository.checkEmployeePhone(phone);

        if(count>0)
            throw new BAuthException("Phone number already present");

        Integer id= employeeRepository.createEmployee(firstName,lastName,password, Employee.Role.EMPLOYEE,phone);

        Employee employee= employeeRepository.findEmployeeById(id);
        return employee;
    }

}
