package com.thanh.foodOrder.service;

import com.thanh.foodOrder.domain.User;
import com.thanh.foodOrder.domain.respone.ResponseUserDTO;
import com.thanh.foodOrder.repository.UserRepository;

import lombok.extern.log4j.Log4j2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseUserDTO createUser(User user) {

        User savedUser = userRepository.save(user);
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
}
