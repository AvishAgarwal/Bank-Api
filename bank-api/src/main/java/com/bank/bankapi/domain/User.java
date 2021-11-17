package com.bank.bankapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    int user_id ;
    String first_name;
    String last_name;
    String phone ;
    int created_by;
    String password ;
    int current_account_number ;
    int saving_account_number ;
    int loan_account_number ;
    int salary_account_number;
    Status kyc_status ;
    String adhaar_number ;
    boolean is_deleted ;
    String created_at ;
    String last_updated_at ;

        public enum Status{
        VERIFIED,
        UNVERIFIED,
        REJECTED
        }
}
