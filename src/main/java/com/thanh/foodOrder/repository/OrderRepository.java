package com.thanh.foodOrder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.thanh.foodOrder.domain.Order;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.domain.Voucher;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUser(User user);

    boolean existsByUserAndVoucher(User user, Voucher voucher);
}
