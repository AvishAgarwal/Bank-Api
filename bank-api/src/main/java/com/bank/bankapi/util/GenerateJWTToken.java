package com.bank.bankapi.util;

import com.bank.bankapi.Constants;
import com.bank.bankapi.domain.Employee;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Service
public class GenerateJWTToken {
    public Map<String, String> generateJWTToken(Employee employee) {
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
