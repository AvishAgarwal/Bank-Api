package com.bank.bankapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    int transaction_id ;
    int from_id ;
    int to_id ;
    double amount;
    double from_balance;
    double to_balance ;
    String created_at;
}
