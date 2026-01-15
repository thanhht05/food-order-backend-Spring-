package com.thanh.foodOrder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanh.foodOrder.domain.Product;
import com.thanh.foodOrder.service.ProductService;
import com.thanh.foodOrder.service.UploadFileService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    private final UploadFileService uploadFileService;
    private final ProductService productService;

    public ProductController(UploadFileService uploadFileService, ProductService productService) {
        this.uploadFileService = uploadFileService;
        this.productService = productService;
    }

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> handleCreateProduct(@RequestPart("product") String productJson,
            @RequestPart("file") MultipartFile file) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        String imagePath = uploadFileService.uploadFile(product.getCategory().getName(), file);
        product.setImg(imagePath);
        Product savedProduct = productService.createProduct(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

}
