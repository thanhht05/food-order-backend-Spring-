package com.thanh.foodOrder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thanh.foodOrder.domain.CartDetail;
import com.thanh.foodOrder.dtos.CartDetailsDTO;
import com.thanh.foodOrder.dtos.CartRequestDTO;
import com.thanh.foodOrder.service.CartService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/carts")
    public ResponseEntity<Void> addProcutToCart(@RequestBody CartRequestDTO request) {
        cartService.addProductsToCart(request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("/cartDetail/product/{id}")
    public ResponseEntity<Void> handleDeleteCartDetail(@PathVariable("id") Long id) {
        this.cartService.removeProductFromCart(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/cartDetails")
    public ResponseEntity<List<CartDetailsDTO>> getAllCarts() {
        return ResponseEntity.status(HttpStatus.OK).body(this.cartService.getAllCartDetail());
    }

}
