package com.bank.bankapi.services;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.User;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.exceptions.BBadRequestException;
import com.bank.bankapi.exceptions.BNotFoundException;
import com.bank.bankapi.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserServices {
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountService accountService;

    @Override
    public User registerUser(String firstName, String lastName, String password, int employeeId, String phone) throws BBadRequestException {
        logger.info("Phone number received {}",phone);
        if (phone.length() != 10)
            throw new BBadRequestException("Invalid Phone Number");
        Integer count = userRepository.checkUserPhone(phone);
        if (count > 0)
            throw new BBadRequestException("Phone number already present");
        User user = new User();
        user.setFirst_name(firstName);
        user.setLast_name(lastName);
        user.setPassword(password);
        user.setCreated_by(employeeId);
        user.setPhone(phone);
        user.setKyc_status(User.Status.UNVERIFIED);
        Integer id = userRepository.createUser(user);
        logger.info("User created with id {}",id);
        User user2 = userRepository.findUserById(id);
        return user2;
    }

    @Override
    public boolean updateKyc(String phone, String adhaar, User.Status status) throws BNotFoundException {
        logger.info("Phone number received {}",phone);
        logger.info("Adhaar number received {}",adhaar);
        if (phone.length() != 10)
            throw new BNotFoundException("Invalid Phone Number");

        return userRepository.updateKyc(phone, adhaar, status);
    }

    @Override
    public boolean deleteUser(int user_id) throws BNotFoundException {
        logger.info("User received with id {}",user_id);
        User user = userRepository.findUserById(user_id);
        if (user == null) {
            logger.error("User is not present with id {}",user_id);
            throw new BNotFoundException("User do not exists");
        }
        boolean flag = true;
        if (user.getCurrent_account_number() != 0) {
            logger.info("Deleting Current Account with number {}",user.getCurrent_account_number());
            flag = accountService.deleteAccount(user.getCurrent_account_number());

        }
        if (user.getSaving_account_number() != 0) {
            logger.info("Deleting Saving Account with number {}",user.getSaving_account_number());
            flag = accountService.deleteAccount(user.getSaving_account_number());
        }
        if (user.getLoan_account_number() != 0) {
            logger.info("Deleting Loan Account with number {}",user.getLoan_account_number());
            flag = accountService.deleteAccount(user.getLoan_account_number());

        }
        if (user.getSalary_account_number() != 0) {
            logger.info("Deleting Salary Account with number {}",user.getSalary_account_number());
            flag = accountService.deleteAccount(user.getSalary_account_number());

        }
        if (!flag) {
            throw new BNotFoundException("Unable to delete account");
        }
        return userRepository.deleteUserById(user_id);
    }

    @Override
    public Integer createAccount(String id, Account.Type type, String balance) throws BBadRequestException {

        User user = userRepository.findUserById(Integer.valueOf(id));
        if (user == null) {
            logger.error("Unable to get user with id {}",id);
            throw new BBadRequestException("No such id exists");
        }

        if (user.getKyc_status() == User.Status.REJECTED || user.getKyc_status() == User.Status.UNVERIFIED) {
            throw new BBadRequestException("Please get the kyc verified");
        }

        Account account = new Account();
        account.setUser_id(Integer.parseInt(id));
        account.setCurrent_balance(Double.parseDouble(balance));
        account.setType(type);
        boolean hasAccount = false;
        switch (account.getType()) {
            case LOAN:
                if (user.getLoan_account_number() != 0)
                    hasAccount = true;
                break;
            case SALARY:
                if (user.getSalary_account_number() != 0)
                    hasAccount = true;
                break;
            case SAVING:
                if (user.getSaving_account_number() != 0)
                    hasAccount = true;
                break;
            case CURRENT:
                if (user.getCurrent_account_number() != 0)
                    hasAccount = true;
                break;
        }
        if (hasAccount) {
            throw new BBadRequestException("Account Already Exists");
        }

        int accountNumber = accountService.createAccount(account);

        switch (account.getType()) {
            case LOAN:
                user.setLoan_account_number(accountNumber);
                logger.info("Loan account created");
                break;
            case SALARY:
                user.setSalary_account_number(accountNumber);
                logger.info("Salary account created");
                break;
            case SAVING:
                user.setSaving_account_number(accountNumber);
                logger.info("Saving account create");
                break;
            case CURRENT:
                user.setCurrent_account_number(accountNumber);
                logger.info("Current account create");
                break;
            default:
                throw new BBadRequestException("Invalid Account type");
        }
        boolean flag = userRepository.updateAccounts(user);
        if (flag == false)
            throw new BBadRequestException("Unable to create Account");
        return accountNumber;
    }
}
