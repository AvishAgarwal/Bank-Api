package com.bank.bankapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    int account_number;
    int user_id;
    Type type;
    double current_balance;
    boolean is_deleted;
    String created_at;
    String last_updated_at;

    public enum Type {
        CURRENT,
        SAVING,
        LOAN,
        SALARY
    }
}
