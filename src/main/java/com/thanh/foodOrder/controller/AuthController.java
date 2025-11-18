package com.thanh.foodOrder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thanh.foodOrder.domain.ResponseLoginDTO;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.domain.request.RequestLoginDTO;
import com.thanh.foodOrder.service.UserService;
import com.thanh.foodOrder.util.anotation.ApiMessage;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    @ApiMessage("Login ")
    public ResponseEntity<ResponseLoginDTO> handleLogin(@RequestBody RequestLoginDTO loginDTO) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                loginDTO.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = this.userService.getUserByEmail(loginDTO.getUsername());
        ResponseLoginDTO res = new ResponseLoginDTO();
        res.setEmail(user.getEmail());
        res.setId(user.getId());
        res.setFullname(user.getFullName());
        res.setRole(user.getRole());

        return ResponseEntity.ok().body(res);
    }

}
