package com.thanh.foodOrder.service;

import org.springframework.stereotype.Service;

import com.thanh.foodOrder.domain.Category;
import com.thanh.foodOrder.domain.Product;
import com.thanh.foodOrder.repository.ProductRepository;
import com.thanh.foodOrder.util.exception.CommonException;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Product createProduct(Product product) {

        // 1. Load managed Category from DB first
        Category category = this.categoryService
                .getCategoryByName(product.getCategory().getName());

        // 2. Check duplicate using managed Category
        boolean isDuplicate = productRepository.existsByNameAndCategory(
                product.getName(), category);

        if (isDuplicate) {
            throw new CommonException(
                    "Product '" + product.getName() + "' already exists in this category");
        }

        // 3. Set managed Category into Product
        product.setCategory(category);

        log.info("Product created successfully");

        // 4. Save Product
        return this.productRepository.save(product);
    }

}
