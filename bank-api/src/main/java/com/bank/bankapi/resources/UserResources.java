package com.bank.bankapi.resources;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.domain.User;
import com.bank.bankapi.repositories.EmployeeRepository;
import com.bank.bankapi.repositories.UserRepository;
import com.bank.bankapi.services.AccountService;
import com.bank.bankapi.services.UserServices;
import lombok.NonNull;
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

    @Autowired
    UserServices userServices;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> registerUser(@RequestBody Map<String,Object> data, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        if(employeeAuth==null || employeeAuth.is_active() == false)
        {
            Map<String, String> map= new HashMap<>();
            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        String firstName = (String)data.get("firstName");
        String lastName= (String)data.get("lastName");
        String phone= (String)data.get("phone");
        String password=(String)data.get("password");

        User user= userServices.registerUser(firstName,lastName,password,userID ,phone);

        Map<String,String> map= new HashMap<>();
        map.put("id", String.valueOf(user.getUser_id()));
        return new ResponseEntity<>(map,HttpStatus.OK);

    }

    @PutMapping("/kyc")
    public ResponseEntity<Map<String,String>> updateKyc(@RequestBody Map<String,Object> data, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        if(employeeAuth==null || employeeAuth.is_active() == false)
        {
            Map<String, String> map= new HashMap<>();
            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

        String phone= (String)data.get("phone");
        String adhaar=(String)data.get("adhaar");
        String status= (String) data.get("status");

        boolean flag= userServices.updateKyc(phone,adhaar,User.Status.valueOf(status));

        Map<String,String> map= new HashMap<>();
        if(flag)
        map.put("message","KYC updated");
        else
            map.put("message","Unable to update Kyc");
        return new ResponseEntity<>(map,HttpStatus.OK);

    }

    @PostMapping("/create-account")
    public ResponseEntity<Map<String,String>> createAccount(@RequestBody Map<String,Object> data, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String, String> map= new HashMap<>();
        if(employeeAuth==null || employeeAuth.is_active() == false)
        {

            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        Account.Type type;
        String id= (String)data.get("id");
        try
        {
            type = Account.Type.valueOf((String) data.get("type"));
        }
        catch (Exception e)
        {
            map.put("message","Incorrect Type");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        String balance= (String)data.get("balance");

        int accountNumber=userServices.createAccount(id,type,balance);
        map.put("account_number", String.valueOf(accountNumber));

        return new ResponseEntity<>(map,HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,String>> getUserInfo( @PathVariable("id") String id, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String,String> map= new HashMap<>();
        if(employeeAuth==null || employeeAuth.is_active() == false)
        {

            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

        User user= userRepository.findUserById(Integer.parseInt(id));
        if(user==null)
        {
            map.put("message","User do not exist");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        map.put("Name", user.getFirst_name()+" "+user.getLast_name());
        map.put("Phone",user.getPhone());
        if(user.getCurrent_account_number()!=0)
        {
            Account account=accountService.getAccountByAccNo(user.getCurrent_account_number());
            map.put("Current_Account_Number",String.valueOf(user.getCurrent_account_number()));
            map.put("Current_Account_Balance",String.valueOf(account.getCurrent_balance()));
        }
        if(user.getSaving_account_number()!=0)
        {
            Account account=accountService.getAccountByAccNo(user.getSaving_account_number());
            map.put("Saving_Account_Number",String.valueOf(user.getSaving_account_number()));
            map.put("Saving_Account_Balance",String.valueOf(account.getCurrent_balance()));
        }
        if(user.getLoan_account_number()!=0)
        {
            Account account=accountService.getAccountByAccNo(user.getLoan_account_number());
            map.put("Loan_Account_Number",String.valueOf(user.getLoan_account_number()));
            map.put("Loan_Account_Balance",String.valueOf(account.getCurrent_balance()));
        }
        if(user.getSalary_account_number()!=0)
        {
            Account account=accountService.getAccountByAccNo(user.getSalary_account_number());
            map.put("Salary_Account_Number",String.valueOf(user.getSalary_account_number()));
            map.put("Salary_Account_Balance",String.valueOf(account.getCurrent_balance()));
        }

        return new ResponseEntity<>(map,HttpStatus.OK);

    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<Map<String,String>> deleteUser( @PathVariable("id") String id, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String,String> map= new HashMap<>();
        if(employeeAuth==null || employeeAuth.is_active() == false)
        {

            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }


        boolean flag=userServices.deleteUser(Integer.parseInt(id));
        if(flag)
        {
            map.put("message","Deleted Successfully");
        }
        else
        {
            map.put("message","unable to delete");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(map,HttpStatus.OK);

    }

}
