package com.bank.bankapi.resources;

import com.bank.bankapi.Constants;
import com.bank.bankapi.domain.Employee;
import com.bank.bankapi.repositories.EmployeeRepository;
import com.bank.bankapi.services.EmployeeService;
import com.bank.bankapi.util.GenerateJWTToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    Logger logger= LoggerFactory.getLogger(EmployeeResources.class);
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
        Map<String, String> map= new HashMap<>();
        if(employeeAuth==null ||employeeAuth.getRole() != Employee.Role.ADMIN || employeeAuth.is_active() == false)
        {

            map.put("message","You are not authorized to do this function");
            logger.error("Employee is not authorized/logged out with id {}",userID);
            return new ResponseEntity<>(map,HttpStatus.UNAUTHORIZED);
        }
        String firstName = (String)data.get("firstName");
        String lastName= (String)data.get("lastName");
        String phone= (String)data.get("phone");
        String password=(String)data.get("password");
        if(firstName==null||lastName==null||phone==null||password==null||firstName.isBlank()||lastName.isBlank()||phone.isBlank()||password.isBlank())
        {
            logger.error("Data not present");
            map.put("message","Data is absent");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);

        }
        Employee employee= employeeService.registerEmployee(firstName,lastName,password, Employee.Role.EMPLOYEE,phone);
        logger.info("Employee registered with id {}",employee.getUser_id());
        map.put("id", String.valueOf(employee.getUser_id()));
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
        logger.info("Login :Details received phone {} ",phone);
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
        if(employeeAuth==null || employeeAuth.is_active() == false)
        {
            map.put("message","You are not authorized to do this function");
            logger.error("Employee is not authorized/logged out with id {}",userID);
            return new ResponseEntity<>(map,HttpStatus.UNAUTHORIZED);
        }

        Employee employee= employeeService.logout(employeeAuth);
        logger.info("Logout Successfull for employee {}",employee.getUser_id());
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
            logger.error("Employee is not authorized/logged out with id {}",userID);
            return new ResponseEntity<>(map,HttpStatus.UNAUTHORIZED);
        }
        String phone= (String)data.get("phone");
        if(phone==null||phone.isBlank())
        {
            logger.error("Data not present");
            map.put("message","Data is absent");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        boolean flag= employeeService.deleteEmployee(phone);
        if(flag) {
            logger.info("Employee with phone {} deleted successfully",phone);
            map.put("message", "Employee Deleted Successfully");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        else {
            logger.error("Unable to delete the Employee with phone {}",phone);
            map.put("message", "Unable to Delete the Employee");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

    }


}
