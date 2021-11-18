package com.bank.bankapi.resources;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.repositories.EmployeeRepository;
import com.bank.bankapi.services.AccountService;
import com.bank.bankapi.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountResources {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;
    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<Map<String,String>> getBalance(@PathVariable("accountNumber") String accountNumber,
                                                        HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String, String> map= new HashMap<>();
        if(employeeAuth==null )
        {
            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

        if(accountNumber == null || accountNumber.length()==0)
        {
            map.put("message","Give Account number");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        Account account = accountService.getAccountByAccNo(Integer.parseInt(accountNumber));
        if(account == null)
        {
            map.put("message","unable to get account");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        map.put("current_balance",String.valueOf(account.getCurrent_balance()));
        map.put("type",String.valueOf(account.getType()));

        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String,String>> createAccount(@RequestBody Map<String,Object> data, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String, String> map= new HashMap<>();
        if(employeeAuth==null )
        {

            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        int from = Integer.parseInt((String)data.get("from"));
        int to= Integer.parseInt((String)data.get("to"));
        double amount= Double.parseDouble((String)data.get("amount"));

        Account fromAcc= accountService.getAccountByAccNo(from);
        Account toAcc=accountService.getAccountByAccNo(to);
        if(fromAcc.getCurrent_balance()<amount)
        {
            map.put("message","Insufficient Balance");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
        int transId=transactionService.createTransaction(fromAcc,toAcc,amount);
        map.put("transaction_id",String.valueOf(transId));
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
}
