package com.bank.bankapi.resources;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.repositories.EmployeeRepository;
import com.bank.bankapi.services.AccountService;
import com.bank.bankapi.services.TransactionService;
import com.itextpdf.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountResources {
    Logger logger= LoggerFactory.getLogger(AccountResources.class);
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;

    /**
     * Getting balance for given account number
     *
     * @param accountNumber
     * @param request       employee data
     * @return balance
     */
    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<Map<String, String>> getBalance(@PathVariable("accountNumber") String accountNumber,
                                                          HttpServletRequest request) {
        int userID = (Integer) request.getAttribute("userId");
        Employee employeeAuth = employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if (employeeAuth == null || employeeAuth.is_active() == false) {
            map.put("message", "You are not authorized to do this function");
            logger.error("Employee is not authorized/logged out with id {}",userID);
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        if (accountNumber == null || accountNumber.length() == 0) {
            map.put("message", "Give Account number");
            logger.error("Account number not provided");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        Account account = accountService.getAccountByAccNo(Integer.parseInt(accountNumber));
        if (account == null) {
            logger.error("Account not found");
            map.put("message", "unable to get account");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        map.put("current_balance", String.valueOf(account.getCurrent_balance()));
        map.put("type", String.valueOf(account.getType()));

        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    /**
     * Transfer money from one account to another
     *
     * @param data    account from which we want to send money, account in which we want to receive money
     * @param request Employee data
     * @return Transaction id
     */
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        int userID = (Integer) request.getAttribute("userId");
        Employee employeeAuth = employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if (employeeAuth == null || employeeAuth.is_active() == false) {
            logger.error("Employee is not authorized/logged out with id {}",userID);
            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        String fromstr=(String) data.get("from");
        String toStr=(String) data.get("to");
        String amountstr=(String) data.get("amount");
        if(fromstr==null||toStr==null||amountstr==null||fromstr.isBlank()||toStr.isBlank()||amountstr.isBlank())
        { logger.error("Data not present");
            map.put("message","Data is absent");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        int from = Integer.parseInt(fromstr);
        int to = Integer.parseInt(toStr);
        double amount = Double.parseDouble(amountstr);

        Account fromAcc = accountService.getAccountByAccNo(from);
        Account toAcc = accountService.getAccountByAccNo(to);
        if (fromAcc.getCurrent_balance() < amount) {
            map.put("message", "Insufficient Balance");
            logger.info("Balance is Insufficient");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        int transId = transactionService.createTransaction(fromAcc, toAcc, amount);
        logger.info("Transaction Successful with id {}",transId);
        map.put("transaction_id", String.valueOf(transId));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * Generate Pdf for transactions
     *
     * @param data    Date to start , date to stop, Account number
     * @param request Employee data
     * @return success message + create a pdf on local
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, String>> getTransactions(@RequestBody Map<String, Object> data, HttpServletRequest request) throws FileNotFoundException, DocumentException {
        int userID = (Integer) request.getAttribute("userId");
        Employee employeeAuth = employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if (employeeAuth == null || employeeAuth.is_active() == false) {
            logger.error("Employee is not authorized/logged out with id {}",userID);
            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        String start = (String) data.get("start");
        String stop = (String) data.get("stop");
        String accountNostr=(String) data.get("account");
        if (start == null || stop == null || accountNostr == null || start.isBlank() || stop.isBlank() || accountNostr.isBlank()) {
            logger.error("Data not present");
            map.put("message", "Data is absent");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        int accountNo = Integer.parseInt(accountNostr);

        Account acc = accountService.getAccountByAccNo(accountNo);

        if (acc == null) {
            map.put("message", "Account do not exist");
            logger.info("Unable to get the account");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        transactionService.getTransaction(start, stop, accountNo);
        logger.info("Pdf created successfully");
        map.put("message", "Pdf created");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * Api to add interest on annual bases, I have considered that money is transfered from a bank's own account to user's account
     *
     * @param data
     * @param request
     * @return
     * @throws ParseException
     */
    @PutMapping("/interest")
    public ResponseEntity<Map<String, String>> addInterest(@RequestBody Map<String, Object> data, HttpServletRequest request) throws ParseException {
        int userID = (Integer) request.getAttribute("userId");
        Employee employeeAuth = employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if (employeeAuth == null || employeeAuth.is_active() == false) {
            logger.error("Employee is not authorized/logged out with id {}",userID);
            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        String fromstr=(String) data.get("from");
        String toStr=(String) data.get("to");
        if(fromstr==null||toStr==null||fromstr.isBlank()||toStr.isBlank())
        {
            logger.error("Data not present");
            map.put("message", "Data is absent");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        int from = Integer.parseInt(fromstr);
        int to = Integer.parseInt(toStr);

        Account fromAcc = accountService.getAccountByAccNo(from);
        Account toAcc = accountService.getAccountByAccNo(to);

        if (fromAcc == null || toAcc == null) {
            map.put("message", "Account do not exist");
            logger.error("Unable to get the account");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        int id = transactionService.addInterest(fromAcc, toAcc);
        logger.info("Interest added successfully");
        map.put("transaction_id", String.valueOf(id));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
