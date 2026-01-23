package com.thanh.foodOrder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thanh.foodOrder.domain.BookingTable;
import com.thanh.foodOrder.domain.ResultPaginationDTO;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.domain.respone.user.ResponseUserDTO;
import com.thanh.foodOrder.repository.BookingTableRepository;
import com.thanh.foodOrder.util.exception.CommonException;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BookingTableService {
    private final BookingTableRepository bookingTableRepository;

    public BookingTableService(BookingTableRepository bookingTableRepository) {
        this.bookingTableRepository = bookingTableRepository;
    }

    public boolean checkExistTableByName(String name) {
        return this.bookingTableRepository.existsByName(name);
    }

    public BookingTable getTableById(Long id) {
        return this.bookingTableRepository.findById(id).orElseThrow(() -> {
            log.warn("Table with id: {} not found", id);
            return new CommonException("Table with id " + id + " not found");
        });
    }

    public BookingTable createTable(BookingTable bookingTable) {
        log.info("Creating table with id: {}", bookingTable.getId());

        if (checkExistTableByName(bookingTable.getName())) {
            log.warn("Table {} already exists", bookingTable.getId());
            throw new CommonException("Table " + bookingTable.getId() + " already exists");

        }
        log.info("Table created successfully with id: {}", bookingTable.getId());

        return this.bookingTableRepository.save(bookingTable);

    }

    public BookingTable updateTable(BookingTable bookingTable) {
        log.info("Updating table with id: {}", bookingTable.getId());

        BookingTable bookingTableDb = getTableById(bookingTable.getId());
        if (checkExistTableByName(bookingTable.getName())) {
            log.warn("Table {} already exists", bookingTable.getId());
            throw new CommonException("Table " + bookingTable.getId() + " already exists");

        }

        bookingTableDb.setName(bookingTable.getName());
        bookingTable.setTableStatus(bookingTable.getTableStatus());
        log.info("Table with id {} updated successfully", bookingTableDb.getId());

        return this.bookingTableRepository.save(bookingTableDb);
    }

    public void deleteTable(Long id) {
        log.info("Deleting table with id: {}", id);

        BookingTable bookingTableDb = getTableById(id);
        log.info("Table with id {} deleted successfully", bookingTableDb.getId());

        this.bookingTableRepository.delete(bookingTableDb);

    }

    public ResultPaginationDTO getAllTables(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // no filter
        Page<BookingTable> tablePages;
        if (keyword == null || keyword.isBlank()) {
            tablePages = this.bookingTableRepository.findAll(pageable);
        } else {

            tablePages = this.bookingTableRepository.findByNameIgnoreCase(keyword, pageable);
        }

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(tablePages.getTotalPages());
        meta.setTotalElements(tablePages.getTotalElements());

        rs.setMeta(meta);

        List<BookingTable> bookingTables = tablePages.getContent()
                .stream()
                .map(table -> new BookingTable(table.getId(), table.getName(), table.getTableStatus(),
                        table.getCreatedBy(), table
                                .getUpdatedBy(),
                        table.getCreatedAt(), table.getUpdatedAt()))
                .collect(Collectors.toList());
        rs.setResults(bookingTables);
        return rs;
    }

}
