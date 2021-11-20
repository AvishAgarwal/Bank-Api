package com.bank.bankapi.resources;

import com.bank.bankapi.Constants;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.repositories.EmployeeRepository;
import com.bank.bankapi.services.EmployeeService;
import com.bank.bankapi.util.GenerateJWTToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class has all the api which we need to do all the admin can do related to employee
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeResources {

    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    GenerateJWTToken generateJWTToken;

    /**Api to register employee
     *
     * @param data data contains first name , last name, phone , password
     * @param request request contains the authorization token
     * @return it return success message
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> registerEmployee(@RequestBody Map<String,Object> data, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        if(employeeAuth==null ||employeeAuth.getRole() != Employee.Role.ADMIN || employeeAuth.is_active() == false)
        {
            Map<String, String> map= new HashMap<>();
            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map,HttpStatus.UNAUTHORIZED);
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

    /** Api to login
     *
     * @param data contains phone and password
     * @return Auth token
     */
    @PutMapping("/login")
    public ResponseEntity<Map<String,String>> loginEmployee(@RequestBody Map<String,Object> data){

        String phone= (String)data.get("phone");
        String password=(String)data.get("password");
        Employee employee= employeeService.validateEmployee(phone,password,true);

        return new ResponseEntity<>(generateJWTToken.generateJWTToken(employee), HttpStatus.OK);

    }

    /**
     * Log out functionality
     * @param request contains user_id of employee
     * @return Success message
     */
    @PutMapping("/logout")
    public ResponseEntity<Map<String,String>> logoutEmployee( HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if(employeeAuth==null ||employeeAuth.getRole() != Employee.Role.ADMIN || employeeAuth.is_active() == false)
        {
            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map,HttpStatus.UNAUTHORIZED);
        }

        Employee employee= employeeService.logout(employeeAuth);
        map.put("message","Employee has been logged out");
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    /**
     * Soft delete a employee
     * @param data contains phone number of employee
     * @param request   success message
     * @return
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String,String>> deleteEmployee(@RequestBody Map<String,Object> data, HttpServletRequest request){
        int userID= (Integer) request.getAttribute("userId");
        Employee employeeAuth =  employeeRepository.findEmployeeById(userID);
        Map<String, String> map = new HashMap<>();
        if(employeeAuth==null ||employeeAuth.getRole() != Employee.Role.ADMIN || employeeAuth.is_active() == false)
        {
            map.put("message","You are not authorized to do this function");
            return new ResponseEntity<>(map,HttpStatus.UNAUTHORIZED);
        }
        String phone= (String)data.get("phone");
        boolean flag= employeeService.deleteEmployee(phone);
        if(flag) {
            map.put("message", "Employee Deleted Successfully");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            map.put("message", "Unable to Delete the Employee");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

    }


}
