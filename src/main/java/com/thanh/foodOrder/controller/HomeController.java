//package com.thanh.foodOrder.controller;
//
//import com.thanh.foodOrder.domain.User;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//
//@RestController
//public class HomeController {
//    @GetMapping("/")
//    public String getHomePage() {
//        User user1 = new User("Thanh", "0123456789");
//        User user2 = new User("An", "0987654321");
//        User user3 = new User("Binh", "0112233445");
//        ArrayList<User> users = new ArrayList<>();
//        users.add(user1);
//        users.add(user2);
//        users.add(user3);
//        for(User user : users){
//            System.out.println("Name: " + user.getName() + ", Phone: " + user.getPhone());
//        }
//        return "Welcome to the Food Order Application!";
//
//    }
//
//
//
//}
