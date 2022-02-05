package com.crypt.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

    private int user_id;
    private String name;
    private String symbol;
    private int price_usd;

    public User(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }
}
