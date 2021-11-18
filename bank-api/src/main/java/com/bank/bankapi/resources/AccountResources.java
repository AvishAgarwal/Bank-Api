package com.bank.bankapi.resources;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountResources {

    @Autowired
    AccountService accountService;
    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<Map<String,String>> getBalance(@PathVariable("accountNumber") String accountNumber,
                                                        HttpServletRequest request){
        Map<String,String> map=new HashMap<>();
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
}
