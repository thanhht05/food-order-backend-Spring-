package com.thanh.foodOrder.controller;

import com.thanh.foodOrder.domain.Product;
import com.thanh.foodOrder.domain.ResultPaginationDTO;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.service.CategoryService;
import com.thanh.foodOrder.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class HomeController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    // @GetMapping("/")
    // public String getHomePage() {
    // User user1 = new User("Thanh", "0123456789");
    // User user2 = new User("An", "0987654321");
    // User user3 = new User("Binh", "0112233445");
    // ArrayList<User> users = new ArrayList<>();
    // users.add(user1);
    // users.add(user2);
    // users.add(user3);
    // for(User user : users){
    // System.out.println("Name: " + user.getName() + ", Phone: " +
    // user.getPhone());
    // }
    // return "Welcome to the Food Order Application!";

    // }

    @GetMapping("/home")
    public ResponseEntity<ResultPaginationDTO> getHomePage(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size) {

        ResultPaginationDTO result = productService.searchProduct(keyword, categoryId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
