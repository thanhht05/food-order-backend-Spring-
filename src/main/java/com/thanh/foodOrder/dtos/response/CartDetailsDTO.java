package com.thanh.foodOrder.dtos.response;

import java.util.List;
import java.util.Locale.Category;

import com.thanh.foodOrder.domain.ProductImage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDetailsDTO {

    private int quantity;
    private Double totalPrice;

    private ProductInnerCartDetail productInnerCartDetail;

    @Getter
    @Setter
    public static class ProductInnerCartDetail {
        private Long id;
        private String name;
        private Double price;
        private List<ProductImage> lstImg;

    }

}