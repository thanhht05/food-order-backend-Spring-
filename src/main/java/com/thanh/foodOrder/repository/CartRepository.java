package com.thanh.foodOrder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thanh.foodOrder.domain.Cart;
import com.thanh.foodOrder.domain.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(long id);
}
