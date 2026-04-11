package com.thanh.foodOrder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.print.attribute.standard.PageRanges;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.thanh.foodOrder.domain.Category;
import com.thanh.foodOrder.domain.Product;
import com.thanh.foodOrder.domain.ResultPaginationDTO;
import com.thanh.foodOrder.domain.respone.product.ResponseProductDTO;
import com.thanh.foodOrder.domain.respone.role.ResponseRoleDTO;
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

    public void saveProduct(Product product) {
        this.productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id: {} not found", id);
            return new CommonException("Product with id " + id + " not found");
        });
    }

    public ResponseProductDTO createProduct(Product product) {

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

        Product savedProduct = this.productRepository.save(product);

        ResponseProductDTO res = convertToProductDTO(savedProduct);

        return res;
    }

    public ResponseProductDTO updateProduct(Product product) {

        Product productDb = getProductById(product.getId());

        productDb.setName(product.getName());
        productDb.setPrice(product.getPrice());
        productDb.setDescription(product.getDescription());

        // if (product.getImg() != null) {
        // productDb.setImg(product.getImg());
        // }
        if (product.getCategory() != null && product.getCategory().getId() != 0) {
            Category cate = categoryService.getCategoryById(product.getCategory().getId());
            if (cate != null) {
                productDb.setCategory(cate);
            }

        }

        // Category

        productRepository.save(productDb);

        return convertToProductDTO(productDb);
    }

    public ResponseProductDTO convertToProductDTO(Product product) {
        ResponseProductDTO.ProductCate cate = new ResponseProductDTO.ProductCate();

        cate.setId(product.getId());
        cate.setName(product.getCategory().getName());
        List<String> images = (product.getLstImg() != null) ? product.getLstImg() : new ArrayList<>();

        ResponseProductDTO res = new ResponseProductDTO(product.getId(), product.getName(),
                product.getPrice(), images, product.getQuantity(),
                product.getDescription(), cate, product.getCreatedAt(), product.getUpdatedAt());
        return res;

    }

    public ResultPaginationDTO searchProduct(
            String keyword,
            String categoryName,
            int page,
            int size, String sort) {

        Sort sortObj = Sort.by("updatedAt").descending();

        if (sort != null && !sort.isEmpty()) {
            String[] parts = sort.split(",");
            String feild = parts[0];
            String direction = parts[1];

            sortObj = direction.equalsIgnoreCase("desc")
                    ? Sort.by(feild).descending()
                    : Sort.by(feild).ascending();
        }

        Pageable pageable = PageRequest.of(page - 1, size,
                sortObj);
        Page<Product> pages;

        // 1️ No filter
        if ((keyword == null || keyword.isBlank()) && categoryName == null) {
            pages = productRepository.findAll(pageable);

            // 2️ Name + Category
        } else if (keyword != null && !keyword.isBlank() && categoryName != null) {
            pages = productRepository
                    .findByNameContainingIgnoreCaseAndCategory_name(
                            keyword, categoryName, pageable);

            // 3️ Only name
        } else if (keyword != null && !keyword.isBlank()) {
            pages = productRepository
                    .findByNameContainingIgnoreCase(keyword, pageable);

            // 4️ Only category
        } else {
            pages = productRepository
                    .findByCategory_name(categoryName, pageable);
        }

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pages.getTotalPages());
        meta.setTotalElements(pages.getTotalElements());

        rs.setMeta(meta);
        List<ResponseProductDTO> responseProductDTOs = new ArrayList<>();

        for (Product p : pages.getContent()) {
            ResponseProductDTO.ProductCate productCate = new ResponseProductDTO.ProductCate();
            productCate.setId(p.getCategory().getId());
            productCate.setName(p.getCategory().getName());

            List<String> images = (p.getLstImg() != null) ? p.getLstImg() : new ArrayList<>();

            ResponseProductDTO res = new ResponseProductDTO(p.getId(), p.getName(), p.getPrice(), images,
                    p.getQuantity(), p.getDescription(), productCate, p.getCreatedAt(), p.getUpdatedAt());

            responseProductDTOs.add(res);

        }

        rs.setResults(responseProductDTOs);
        return rs;
    }

    public void handleDeleteProduct(Long id) {
        Product product = getProductById(id);

        this.productRepository.delete(product);

        Category category = product.getCategory();

        if (category != null) {
            // check cate have product
            long count = this.productRepository.countByCategory_Id(category.getId());

            if (count == 0) {
                this.categoryService.deleteCategory(category.getId());
            }
        }

    }

    public void checkQuantityProductBeforeAddToCart(Product product, int quantity) {

        if (product.getQuantity() < quantity) {
            throw new CommonException(
                    "Product " + product.getId() + " does not have enough stock");
        }
    }

}
