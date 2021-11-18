package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.domain.User;
import com.bank.bankapi.exceptions.BAuthException;

public interface UserServices {
    User registerUser(String firstName, String lastName, String password, int employeeId, String phone) throws BAuthException;

    boolean updateKyc(String phone, String adhaar, User.Status status) throws BAuthException;

    boolean deleteUser(int user_id) throws BAuthException;

    Integer createAccount(String id, Account.Type type, String balance ) throws BAuthException;
}
