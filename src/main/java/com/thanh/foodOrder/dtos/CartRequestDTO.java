package com.thanh.foodOrder.dtos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequestDTO {
    private List<CartItemRequestDTO> items;
}
