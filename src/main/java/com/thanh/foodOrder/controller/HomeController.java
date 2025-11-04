package com.thanh.foodOrder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String getHomePage(){
        return "abc 123a update 123 hahah con cho nay  cut ra ngoai hhaah";
    }
}
