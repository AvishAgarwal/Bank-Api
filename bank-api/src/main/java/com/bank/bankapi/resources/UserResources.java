package com.bank.bankapi.resources;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.domain.User;
import com.bank.bankapi.repositories.EmployeeRepository;
import com.bank.bankapi.repositories.UserRepository;
import com.bank.bankapi.services.AccountService;
import com.bank.bankapi.services.UserServices;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserResources {
    Logger logger = LoggerFactory.getLogger(UserResources.class);
    @Autowired
    UserServices userServices;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountService accountService;

    /**
     * Register a new user
     *
     * @param data    first name , last name, phone , password
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        int userID = (Integer) request.getAttribute("userId");
        Employee employeeAuth = employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if (employeeAuth == null || employeeAuth.is_active() == false) {

            logger.error("Employee is not authorized/logged out with id {}",userID);
            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String phone = (String) data.get("phone");
        String password = (String) data.get("password");
        if(firstName==null||lastName==null||phone==null||password==null||firstName.isBlank()||lastName.isBlank()||phone.isBlank()||password.isBlank())
        {
            logger.error("Data not present");
            map.put("message","Data is absent");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);

        }
        User user = userServices.registerUser(firstName, lastName, password, userID, phone);

        logger.info("User registered with id {}",user.getUser_id());
        map.put("id", String.valueOf(user.getUser_id()));
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    /**
     * Updating the Kyc of user
     *
     * @param data    phone , aadhar number , status of verification
     * @param request employee data
     * @return success message
     */
    @PutMapping("/kyc")
    public ResponseEntity<Map<String, String>> updateKyc(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        int userID = (Integer) request.getAttribute("userId");
        Employee employeeAuth = employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if (employeeAuth == null || employeeAuth.is_active() == false) {

            logger.error("Employee is not authorized/logged out with id {}",userID);
            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        String phone = (String) data.get("phone");
        String adhaar = (String) data.get("adhaar");
        String status = (String) data.get("status");
        if(phone==null||adhaar==null||status==null||phone.isBlank()||adhaar.isBlank()||status.isBlank())
        {
            logger.error("Data not present");
            map.put("message","Data is absent");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        boolean flag = userServices.updateKyc(phone, adhaar, User.Status.valueOf(status));

        if (flag) {
            map.put("message", "KYC updated");
            logger.info("Kyc updated");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("message", "Unable to update Kyc");
            logger.error("Unable to update kyc");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Create a new account attach it to a user
     *
     * @param data    user id
     * @param request employee data
     * @return Account number
     */
    @PostMapping("/create-account")
    public ResponseEntity<Map<String, String>> createAccount(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        int userID = (Integer) request.getAttribute("userId");
        Employee employeeAuth = employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if (employeeAuth == null || employeeAuth.is_active() == false) {
            logger.error("Employee is not authorized/logged out with id {}",userID);
            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        Account.Type type;
        String id = (String) data.get("id");
        if(id==null||id.isBlank())
        {
            logger.error("Data not present");
            map.put("message","Data is absent");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        try {
            type = Account.Type.valueOf((String) data.get("type"));
        } catch (Exception e) {
            map.put("message", "Incorrect Type");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        String balance = (String) data.get("balance");

        int accountNumber = userServices.createAccount(id, type, balance);
        logger.info("Account created with id {}",accountNumber);
        map.put("account_number", String.valueOf(accountNumber));

        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    /**
     * Get user and his accounts
     *
     * @param id      user id
     * @param request Employee data
     * @return User info
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getUserInfo(@PathVariable("id") String id, HttpServletRequest request) {
        int userID = (Integer) request.getAttribute("userId");
        Employee employeeAuth = employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if (employeeAuth == null || employeeAuth.is_active() == false) {
            logger.error("Employee is not authorized/logged out with id {}",userID);
            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findUserById(Integer.parseInt(id));
        if (user == null) {
            map.put("message", "User do not exist");
            logger.error("Unable to get the user with id {}",id);
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        map.put("Name", user.getFirst_name() + " " + user.getLast_name());
        map.put("Phone", user.getPhone());
        if (user.getCurrent_account_number() != 0) {
            Account account = accountService.getAccountByAccNo(user.getCurrent_account_number());
            map.put("Current_Account_Number", String.valueOf(user.getCurrent_account_number()));
            map.put("Current_Account_Balance", String.valueOf(account.getCurrent_balance()));
            logger.info("Current Account added");
        }
        if (user.getSaving_account_number() != 0) {
            Account account = accountService.getAccountByAccNo(user.getSaving_account_number());
            map.put("Saving_Account_Number", String.valueOf(user.getSaving_account_number()));
            map.put("Saving_Account_Balance", String.valueOf(account.getCurrent_balance()));
            logger.info("Saving account added");
        }
        if (user.getLoan_account_number() != 0) {
            Account account = accountService.getAccountByAccNo(user.getLoan_account_number());
            map.put("Loan_Account_Number", String.valueOf(user.getLoan_account_number()));
            map.put("Loan_Account_Balance", String.valueOf(account.getCurrent_balance()));
            logger.info("Loan account added");
        }
        if (user.getSalary_account_number() != 0) {
            Account account = accountService.getAccountByAccNo(user.getSalary_account_number());
            map.put("Salary_Account_Number", String.valueOf(user.getSalary_account_number()));
            map.put("Salary_Account_Balance", String.valueOf(account.getCurrent_balance()));
            logger.info("Salary account added");
        }

        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    /**
     * Delete a user and his/her connected accounts
     *
     * @param id      user id
     * @param request employee id
     * @return success message
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("id") String id, HttpServletRequest request) {
        int userID = (Integer) request.getAttribute("userId");
        Employee employeeAuth = employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if (employeeAuth == null || employeeAuth.is_active() == false) {
            logger.error("Employee is not authorized/logged out with id {}",userID);
            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }


        boolean flag = userServices.deleteUser(Integer.parseInt(id));
        if (flag) {
            map.put("message", "Deleted Successfully");
        } else {
            map.put("message", "unable to delete");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(map, HttpStatus.OK);

    }

}
