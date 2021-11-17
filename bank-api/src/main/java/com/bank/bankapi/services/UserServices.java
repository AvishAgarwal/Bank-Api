package com.bank.bankapi.services;

import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.domain.User;
import com.bank.bankapi.exceptions.BAuthException;

public interface UserServices {
    User registerUser(String firstName, String lastName, String password, int employeeId, String phone) throws BAuthException;
}
