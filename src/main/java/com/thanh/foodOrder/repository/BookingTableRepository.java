package com.thanh.foodOrder.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.thanh.foodOrder.domain.BookingTable;

@Repository
public interface BookingTableRepository
                extends JpaRepository<BookingTable, Long>, JpaSpecificationExecutor<BookingTable> {
        Optional<BookingTable> findById(Long id);

        boolean existsByName(String name);

        Page<BookingTable> findByNameIgnoreCase(String name, Pageable pageable);

}
