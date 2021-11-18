package com.bank.bankapi.repositories;

import com.bank.bankapi.domain.User;
import com.bank.bankapi.exceptions.BAuthException;

public interface UserRepository {
    Integer createUser(User user) throws BAuthException;

    Integer checkUserPhone(String phone);

    User findUserById(int user_id);

    boolean updateKyc(String phone, String adhaar, User.Status status) throws BAuthException;

    boolean updateAccounts(User user);
}
