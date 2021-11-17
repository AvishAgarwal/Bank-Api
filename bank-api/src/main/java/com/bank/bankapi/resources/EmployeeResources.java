package com.bank.bankapi.resources;

import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class EmployeeResources {

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> registerEmployee(@RequestBody Map<String,Object> data){
        String firstName = (String)data.get("firstName");
        String lastName= (String)data.get("lastName");
        String phone= (String)data.get("phone");
        String password=(String)data.get("password");
        Employee employee= employeeService.registerEmployee(firstName,lastName,password, Employee.Role.EMPLOYEE,phone);
        Map<String, String> map= new HashMap<>();
        map.put("message","Registered Successfully");
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> loginEmployee(@RequestBody Map<String,Object> data){

        String phone= (String)data.get("phone");
        String password=(String)data.get("password");
        Employee employee= employeeService.validateEmployee(phone,password);
        Map<String, String> map= new HashMap<>();
        map.put("message","Login Successful");
        return new ResponseEntity<>(map, HttpStatus.OK);

    }
}
