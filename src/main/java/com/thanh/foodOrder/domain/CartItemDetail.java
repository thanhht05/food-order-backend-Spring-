package com.thanh.foodOrder.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDetail implements Serializable {

    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private String imageUrl;

    @JsonIgnore
    public double getSubtotal() {
        return price * quantity;
    }
}
