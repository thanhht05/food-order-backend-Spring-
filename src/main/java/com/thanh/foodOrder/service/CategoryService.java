package com.thanh.foodOrder.service;

import org.springframework.stereotype.Service;

import com.thanh.foodOrder.domain.Category;
import com.thanh.foodOrder.repository.CategoryRepository;
import com.thanh.foodOrder.util.exception.CommonException;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public boolean checkExistsByName(String name) {
        return this.categoryRepository.existsByName(name);
    }

    public Category createCategory(Category category) {
        if (checkExistsByName(category.getName())) {
            log.warn("Name {} already exists", category.getName());
            throw new CommonException("Name " + category.getName() + " already exists");
        }

        log.info("Category created successfully ");
        return this.categoryRepository.save(category);

    }

    public Category getCategoryById(Long id) {
        return this.categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Category with id: {} not found", id);
            return new CommonException("Category with id " + id + " not found");

        });
    }

    public Category updateCategory(Category category) {
        log.info("Updating category with id: {}", category.getId());

        Category existingCategory = getCategoryById(category.getId());

        // chỉ check trùng nếu name bị thay đổi
        if (!existingCategory.getName().equals(category.getName())
                && checkExistsByName(category.getName())) {
            log.warn("Name {} already exists", category.getName());
            throw new CommonException("Name " + category.getName() + " already exists");
        }

        existingCategory.setName(category.getName());

        log.info("Category updated successfully");
        return categoryRepository.save(existingCategory);
    }

}
