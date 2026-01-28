package com.thanh.foodOrder.dtos.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequestDTO {
    private Long productId;
    private Integer quantity;
}
