package com.thanh.foodOrder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thanh.foodOrder.domain.Category;
import com.thanh.foodOrder.service.CategoryService;
import com.thanh.foodOrder.util.anotation.ApiMessage;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")

public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories")
    @ApiMessage("Create a category")
    public ResponseEntity<Category> handleCreateCategory(@Valid @RequestBody Category category) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.createCategory(category));
    }

    @PutMapping("/categories")
    @ApiMessage("Update a category ")
    public ResponseEntity<Category> handleUpdateCategory(@Valid @RequestBody Category category) {

        return ResponseEntity.status(HttpStatus.OK).body(this.categoryService.updateCategory(category));
    }

}
