package com.bank.bankapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Employee {
    int user_id ;
    String first_name ;
    String last_name;
    String phone;
    String  password ;
    Role role ;
    boolean is_deleted ;
    String created_at ;
    String last_updated_at;
    boolean is_active;

    public enum Role {
        ADMIN,
        EMPLOYEE
    }
}

