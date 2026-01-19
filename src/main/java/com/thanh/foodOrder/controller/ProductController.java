package com.thanh.foodOrder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanh.foodOrder.domain.Product;
import com.thanh.foodOrder.domain.ResultPaginationDTO;
import com.thanh.foodOrder.domain.respone.product.ResponseProductDTO;
import com.thanh.foodOrder.service.ProductService;
import com.thanh.foodOrder.service.UploadFileService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<ResponseProductDTO> handleCreateProduct(@RequestPart("product") String productJson,
            @RequestPart(value = "file") MultipartFile file) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        String imagePath = uploadFileService.uploadFile(product.getCategory().getName(), file);
        product.setImg(imagePath);
        ResponseProductDTO savedProduct = productService.createProduct(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseProductDTO> handleUpdateProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        if (file != null && !file.isEmpty()) {
            String imagePath = uploadFileService.uploadFile(
                    product.getCategory().getName(), file);
            product.setImg(imagePath);
        }

        ResponseProductDTO res = productService.updateProduct(product);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> handleDeletProduct(@PathVariable(value = "id") Long id) {

        this.productService.handleDeleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ResponseProductDTO> handelGetProductById(@PathVariable("id") Long id) {
        Product product = this.productService.getProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.productService.convertToProductDTO(product));
    }

    @GetMapping("/products")
    public ResponseEntity<ResultPaginationDTO> getHomePage(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size) {

        ResultPaginationDTO result = productService.searchProduct(keyword, categoryId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
