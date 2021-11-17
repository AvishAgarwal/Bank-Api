package com.bank.bankapi.services;

import com.bank.bankapi.domain.User;
import com.bank.bankapi.exceptions.BAuthException;
import com.bank.bankapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserServices{
    @Autowired
    UserRepository userRepository;
    @Override
    public User registerUser(String firstName, String lastName, String password, int employeeId, String phone) throws BAuthException {
        if(phone.length()!=10)
            throw new BAuthException("Invalid Phone Number");
        Integer count= userRepository.checkUserPhone(phone);
        if(count>0)
            throw new BAuthException("Phone number already present");
        User user= new User();
        user.setFirst_name(firstName);
        user.setLast_name(lastName);
        user.setPassword(password);
        user.setCreated_by(employeeId);
        user.setPhone(phone);
        user.setKyc_status(User.Status.UNVERIFIED);
        Integer id= userRepository.createUser(user);
        User user2= userRepository.findUserById(id);
        return user2;
    }
}
