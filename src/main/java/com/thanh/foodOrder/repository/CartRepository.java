package com.thanh.foodOrder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thanh.foodOrder.domain.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

}
