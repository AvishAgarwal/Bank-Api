package com.bank.bankapi.resources;

import com.bank.bankapi.domain.Account;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.domain.Transaction;
import com.bank.bankapi.repositories.EmployeeRepository;
import com.bank.bankapi.services.AccountService;
import com.bank.bankapi.services.TransactionService;
import com.itextpdf.text.DocumentException;
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
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        if (accountNumber == null || accountNumber.length() == 0) {
            map.put("message", "Give Account number");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        Account account = accountService.getAccountByAccNo(Integer.parseInt(accountNumber));
        if (account == null) {
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

            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        int from = Integer.parseInt((String) data.get("from"));
        int to = Integer.parseInt((String) data.get("to"));
        double amount = Double.parseDouble((String) data.get("amount"));

        Account fromAcc = accountService.getAccountByAccNo(from);
        Account toAcc = accountService.getAccountByAccNo(to);
        if (fromAcc.getCurrent_balance() < amount) {
            map.put("message", "Insufficient Balance");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        int transId = transactionService.createTransaction(fromAcc, toAcc, amount);
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

            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        String start = (String) data.get("start");
        String stop = (String) data.get("stop");
        int accountNo = Integer.parseInt((String) data.get("account"));

        Account Acc = accountService.getAccountByAccNo(accountNo);

        if (Acc == null) {
            map.put("message", "Account do not exist");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        transactionService.getTransaction(start, stop, accountNo);
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
            map.put("message", "You are not authorized to do this function");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        int from = Integer.parseInt((String) data.get("from"));
        int to = Integer.parseInt((String) data.get("to"));

        Account fromAcc = accountService.getAccountByAccNo(from);
        Account toAcc = accountService.getAccountByAccNo(to);

        if (fromAcc == null || toAcc == null) {
            map.put("message", "Account do not exist");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        int id = transactionService.addInterest(fromAcc, toAcc);
        map.put("transaction_id", String.valueOf(id));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
