package com.thanh.foodOrder.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {

    private Long orderId;
    private LocalDateTime orderDate;
    private String status;

    private Double totalPrice;
    private Integer discount;
    private Double finalPrice;

    private List<OrderItemDTO> items;
}
