package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.domain.User;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;

public interface UserServices {
    User registerUser(String firstName, String lastName, String password, int employeeId, String phone) throws BBadRequestException;

    boolean updateKyc(String phone, String adhaar, User.Status status) throws BNotFoundException;

    boolean deleteUser(int user_id) throws BNotFoundException;

    Integer createAccount(String id, Account.Type type, String balance) throws BBadRequestException;
}
