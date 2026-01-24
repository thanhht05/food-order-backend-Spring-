package com.thanh.foodOrder.service;

import org.springframework.stereotype.Service;

import com.thanh.foodOrder.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
