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
        if(employeeAuth==null )
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
        map.put("message","User has been registered");
        return new ResponseEntity<>(map,HttpStatus.OK);

    }

    @PutMapping("/kyc")
    public ResponseEntity<Map<String,String>> updateKyc(@RequestBody Map<String,Object> data, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        if(employeeAuth==null )
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
        if(employeeAuth==null )
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

        User user= userRepository.findUserById(Integer.valueOf(id));
        if(user==null)
        {
            map.put("message","No such id exists");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }

        if(user.getKyc_status()== User.Status.REJECTED || user.getKyc_status() == User.Status.UNVERIFIED)
        {
            map.put("message","Please get the kyc verified");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }

        Account account = new Account();
        account.setUser_id(Integer.valueOf(id));
        account.setCurrent_balance(Double.valueOf(balance));
        account.setType(type);
        boolean hasAccount=false;
        switch (account.getType()){
            case LOAN: if(user.getLoan_account_number()!=0)
                hasAccount=true;
                break;
            case SALARY:if(user.getSalary_account_number()!=0)
                hasAccount=true;
                break;
            case SAVING:if(user.getSaving_account_number()!=0)
                hasAccount=true;
                break;
            case CURRENT:if(user.getCurrent_account_number()!=0)
                hasAccount=true;
                break;
        }
        if(hasAccount)
        {
            map.put("message","Account Already Exists");
            return new ResponseEntity<>(map,HttpStatus.OK);

        }

        int accountNumber= accountService.createAccount(account);

        switch (account.getType()){
            case LOAN: user.setLoan_account_number(accountNumber);
                        break;
            case SALARY:user.setSalary_account_number(accountNumber);
            break;
            case SAVING:user.setSaving_account_number(accountNumber);
            break;
            case CURRENT:user.setCurrent_account_number(accountNumber);
            break;
            default: map.put("message","Invalid Account type");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }

        boolean flag= userRepository.updateAccounts(user);
        if(flag)
        map.put("message","Account Created");
        else
            map.put("message","Unable to create Account");
        return new ResponseEntity<>(map,HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,String>> getUserInfo( @PathVariable("id") String id, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String,String> map= new HashMap<>();
        if(employeeAuth==null )
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
        map.put("Name", user.getFirst_name()+""+user.getLast_name());
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
            map.put("Current_Account_Number",String.valueOf(user.getSaving_account_number()));
            map.put("Current_Account_Balance",String.valueOf(account.getCurrent_balance()));
        }
        if(user.getLoan_account_number()!=0)
        {
            Account account=accountService.getAccountByAccNo(user.getLoan_account_number());
            map.put("Current_Account_Number",String.valueOf(user.getLoan_account_number()));
            map.put("Current_Account_Balance",String.valueOf(account.getCurrent_balance()));
        }
        if(user.getSalary_account_number()!=0)
        {
            Account account=accountService.getAccountByAccNo(user.getSalary_account_number());
            map.put("Current_Account_Number",String.valueOf(user.getSalary_account_number()));
            map.put("Current_Account_Balance",String.valueOf(account.getCurrent_balance()));
        }

        return new ResponseEntity<>(map,HttpStatus.OK);

    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<Map<String,String>> deleteUser( @PathVariable("id") String id, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String,String> map= new HashMap<>();
        if(employeeAuth==null )
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
        boolean flag=true;
        if(user.getCurrent_account_number()!=0)
        {
             flag=accountService.deleteAccount(user.getCurrent_account_number());

        }
        if(user.getSaving_account_number()!=0)
        {
            flag=accountService.deleteAccount(user.getSaving_account_number());
        }
        if(user.getLoan_account_number()!=0)
        {
            flag=accountService.deleteAccount(user.getLoan_account_number());

        }
        if(user.getSalary_account_number()!=0)
        {
            flag=accountService.deleteAccount(user.getSalary_account_number());

        }
        if(!flag)
        {
            map.put("message","Unable to delete account");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }

        flag=userServices.deleteUser(user.getUser_id());
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
