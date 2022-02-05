package com.crypt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Crypt {

    private int id;
    private String symbol;
    private double price_usd;

    public Crypt(int id, String symbol, double price_usd) {
        this.id = id;
        this.symbol = symbol;
        this.price_usd = price_usd;
    }
}
