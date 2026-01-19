package com.thanh.foodOrder.domain.respone.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProductDTO {
    private long id;
    private String name;
    private double price;
    private String img;
    private int quantity;
    private String description;
    private ProductCate productCate;

    @Getter
    @Setter
    public static class ProductCate {
        private long id;
        private String name;
    }
}
