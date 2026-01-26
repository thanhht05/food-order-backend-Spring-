package com.thanh.foodOrder.dtos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequestDTO {
    private List<Long> cartDetailIds; // các dòng trong giỏ
    private String voucherCode;
    private String note;
    private String paymentMethod;
    private Long tableId;
}
