package com.thanh.foodOrder.service;

import com.thanh.foodOrder.domain.ResultPaginationDTO;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.domain.respone.ResponseUserDTO;
import com.thanh.foodOrder.repository.UserRepository;
import com.thanh.foodOrder.util.exception.CommonException;

import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> {
            log.warn("User with id: {} not found", id);
            return new CommonException("User with id " + id + " not found");
        });
    }

    public ResponseUserDTO createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());

        if (checkExistsByEmail(user.getEmail())) {
            log.warn("Email {} already exists", user.getEmail());
            throw new CommonException("Email " + user.getEmail() + " already exists");
        }
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return convertUserToResUserDTO(savedUser);
    }

    public ResponseUserDTO convertUserToResUserDTO(User user) {
        ResponseUserDTO userDTO = new ResponseUserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setCreatedAt(user.getCreatedAt());
        return userDTO;
    }

    public boolean checkExistsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResponseUserDTO updateUser(User user) {
        log.info("Updating user with id: {}", user.getId());

        User existingUser = this.getUserById(user.getId());

        existingUser.setFullName(user.getFullName());
        existingUser.setPhone(user.getPhone());
        // Update other fields as necessary

        User updatedUser = userRepository.save(existingUser);
        log.info("User with id {} updated successfully", updatedUser.getId());
        return convertUserToResUserDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        User user = this.getUserById(id);

        this.userRepository.delete(user);
        log.info("User with id {} deleted successfully", id);

    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("User with email: {} not found", email);
            throw new CommonException("User with " + email + " not found");
        });
    }

    public ResultPaginationDTO getAllUser(Pageable pageable, Specification<User> spec) {
        Page<User> users = this.userRepository.findAll(spec, pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1); // get current page number
        meta.setPageSize(pageable.getPageSize()); // get page-size
        meta.setPages(users.getTotalPages()); // get total pages
        meta.setTotalElements(users.getTotalElements()); // get total elements in database

        resultPaginationDTO.setMeta(meta);

        List<ResponseUserDTO> userDTOs = users.getContent().stream().map(user -> this.convertUserToResUserDTO(user))
                .collect(Collectors.toList());
        resultPaginationDTO.setResults(userDTOs);
        return resultPaginationDTO;
    }

}
