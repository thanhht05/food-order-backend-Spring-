package com.thanh.foodOrder.controller;

import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.domain.respone.ResponseUserDTO;
import com.thanh.foodOrder.service.UserService;
import com.thanh.foodOrder.util.anotation.ApiMessage;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
