package com.bank.bankapi.resources;

import com.bank.bankapi.Constants;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.repositories.EmployeeRepository;
import com.bank.bankapi.services.EmployeeService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class EmployeeResources {

    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeRepository employeeRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> registerEmployee(@RequestBody Map<String,Object> data, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        if(employeeAuth==null ||employeeAuth.getRole() != Employee.Role.ADMIN )
        {
            Map<String, String> map= new HashMap<>();
            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
        String firstName = (String)data.get("firstName");
        String lastName= (String)data.get("lastName");
        String phone= (String)data.get("phone");
        String password=(String)data.get("password");
        Employee employee= employeeService.registerEmployee(firstName,lastName,password, Employee.Role.EMPLOYEE,phone);
        Map<String ,String > map = new HashMap<>();
        map.put("message","Employee has been registered");
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> loginEmployee(@RequestBody Map<String,Object> data){

        String phone= (String)data.get("phone");
        String password=(String)data.get("password");
        Employee employee= employeeService.validateEmployee(phone,password);

        return new ResponseEntity<>(generateJWTToken(employee), HttpStatus.OK);

    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String,String>> deleteEmployee(@RequestBody Map<String,Object> data, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if(employeeAuth==null ||employeeAuth.getRole() != Employee.Role.ADMIN )
        {
            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
        String phone= (String)data.get("phone");
        boolean flag= employeeService.deleteEmployee(phone);
        if(flag)
            map.put("message","Employee Deleted Successfully");
        else
            map.put("message","Unable to Delete the Employee");
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    private Map<String, String> generateJWTToken(Employee employee) {
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, Constants.KEY)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + Constants.VALIDITY))
                .claim("userId", employee.getUser_id())
                .claim("phone", employee.getPhone())
                .claim("firstName", employee.getFirst_name())
                .claim("lastName", employee.getLast_name())
                .claim("role",employee.getRole())
                .compact();
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }
}
