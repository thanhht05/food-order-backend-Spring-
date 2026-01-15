package com.thanh.foodOrder.domain;

import org.hibernate.annotations.ManyToAny;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Name cannot be empty")
    private String name;

    private String description;

    @NotNull
    private Double price;

    private String img;

    @NotNull

    private int quantity;
    @Nullable
    private Double averageRating = 0.0;
    @Nullable
    private int rateCount = 0;

    @ManyToOne
    @JoinColumn(name = "catrgory_id")
    private Category category;
}
