package com.thanh.foodOrder.specification;

import org.springframework.data.jpa.domain.Specification;

import com.thanh.foodOrder.domain.BookingTable;
import com.thanh.foodOrder.domain.Product;
import com.thanh.foodOrder.domain.User;

public class TableSpecification {

    public static Specification<BookingTable> findByStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                return null;
            }
            return cb.equal(root.get("tableStatus"), status);
        };
    }

    public static Specification<BookingTable> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%");
        };
    }

}