package com.thanh.foodOrder.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("userDetailsService")
public class UserDetailsCustomService implements UserDetailsService {
    private final UserService userService;

    public UserDetailsCustomService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.thanh.foodOrder.domain.User user = this.userService.getUserByEmail(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new User(
                user.getEmail(),
                user.getPassword(),
                authorities);

    }

}
