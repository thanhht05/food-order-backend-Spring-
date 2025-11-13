package com.thanh.foodOrder.controller;

import com.thanh.foodOrder.domain.ResultPaginationDTO;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.domain.respone.ResponseUserDTO;
import com.thanh.foodOrder.service.UserService;
import com.thanh.foodOrder.util.anotation.ApiMessage;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResponseUserDTO> handleCreateUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUser(user));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResponseUserDTO> handleUpdateUser(@RequestBody User user) {
        this.userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.updateUser(user));

    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> handelDeleteUser(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get user by id")
    public ResponseEntity<ResponseUserDTO> handleGetUserById(@PathVariable("id") Long id) {
        User user = this.userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertUserToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(Pageable pageable) {
        Specification<User> spec = Specification.allOf();
        ResultPaginationDTO users = this.userService.getAllUser(pageable, spec);
        return ResponseEntity.ok().body(users);
    }

}
