package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.User;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;

public interface UserRepository {
    Integer createUser(User user) throws BBadRequestException;

    Integer checkUserPhone(String phone);

    User findUserById(int user_id);

    boolean updateKyc(String phone, String adhaar, User.Status status) throws BNotFoundException;

    boolean updateAccounts(User user) throws BNotFoundException;

    boolean deleteUserById(int user_id) throws BBadRequestException;
}
