package com.thanh.foodOrder.domain.respone.order.admin;

import java.time.LocalDateTime;

import com.thanh.foodOrder.enums.PaymentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderResponseDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private String status;

    private Double totalPrice;
    private Integer discount;
    private Double finalPrice;
    private Long tableId;
    private PaymentStatus paymentStatus;

}
