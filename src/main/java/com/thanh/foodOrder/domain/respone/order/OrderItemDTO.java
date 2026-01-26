package com.thanh.foodOrder.domain.respone.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;

}
