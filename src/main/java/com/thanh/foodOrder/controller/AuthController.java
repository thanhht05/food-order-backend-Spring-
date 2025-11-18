package com.thanh.foodOrder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thanh.foodOrder.domain.ResponseLoginDTO;
import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.domain.request.RequestLoginDTO;
import com.thanh.foodOrder.service.UserService;
import com.thanh.foodOrder.util.JwtUtil;
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

        // pass username and password
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                loginDTO.getPassword());

        // authenticate=>loadUserByUsername
        Authentication authentication = authenticationManager.authenticate(token);

        // set authentication in SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = this.userService.getUserByEmail(loginDTO.getUsername());

        // generate accessToken
        String accessToken = JwtUtil.generateToken(loginDTO.getUsername());

        ResponseLoginDTO res = new ResponseLoginDTO();
        ResponseLoginDTO.UserLogin userLogin = new ResponseLoginDTO.UserLogin();
        userLogin.setEmail(user.getEmail());
        userLogin.setFullname(user.getFullName());
        userLogin.setId(user.getId());
        userLogin.setRole(user.getRole());

        // generate refreshToken
        String refreshToken = JwtUtil.generateRefreshToken(loginDTO.getUsername());

        // update user with refreshToken
        this.userService.updateUserRefreshToken(loginDTO.getUsername(), refreshToken);

        res.setAccessToken(accessToken);
        res.setUserLogin(userLogin);

        return ResponseEntity.ok().body(res);
    }

}
