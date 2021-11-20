package com.bank.bankapi.services;

import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;
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
    /**
     * Checking employee updating is_active status
     */
    public Employee validateEmployee(String phone, String password, boolean status) throws BAuthException {
        if(phone.length()!=10)
            throw new BAuthException("Invalid Phone Number");
        Employee employee=employeeRepository.findEmployeeByIdandPassword(phone,password);
        employee.set_active(status);
        employeeRepository.updateEmployeeIsActive(employee);

        return employee;
    }

    /**
     * Register new Employee
     * @param firstName
     * @param lastName
     * @param password
     * @param role
     * @param phone
     * @return
     * @throws BAuthException
     */
    @Override
    public Employee registerEmployee( String firstName, String lastName, String password, Employee.Role role, String phone) throws BBadRequestException {
        if(phone.length()!=10)
            throw new BBadRequestException("Invalid Phone Number");
        Integer count = employeeRepository.checkEmployeePhone(phone);

        if(count>0)
            throw new BBadRequestException("Phone number already present");

        Integer id= employeeRepository.createEmployee(firstName,lastName,password, Employee.Role.EMPLOYEE,phone);

        Employee employee= employeeRepository.findEmployeeById(id);
        return employee;
    }

    /**
     *
     * @param phone
     * @return
     * @throws BAuthException
     */
    @Override
    public boolean deleteEmployee(String phone) throws BNotFoundException {
        if(phone.length()!=10)
            throw new BNotFoundException("Invalid Phone Number");
       return employeeRepository.deleteEmployeeByPhone(phone);

    }

    @Override
    public Employee logout(Employee employee) throws BNotFoundException {
        employee.set_active(false);
        employeeRepository.updateEmployeeIsActive(employee);

        return employee;
    }

}
