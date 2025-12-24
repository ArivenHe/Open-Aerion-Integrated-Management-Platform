package com.example.aimp.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.example.aimp.entity.User;
import com.example.aimp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from AIMP Backend!";
    }

    @PostMapping("/login")
    public SaResult login(@RequestParam String username, @RequestParam String password) {
        // Mock login
        if ("admin".equals(username) && "123456".equals(password)) {
            StpUtil.login(1001);
            return SaResult.ok("Login Success");
        }
        return SaResult.error("Login Failed");
    }

    @GetMapping("/info")
    public SaResult info() {
        if(StpUtil.isLogin()) {
             return SaResult.data("Current User ID: " + StpUtil.getLoginId());
        }
        return SaResult.error("Not Logged In");
    }

    @PostMapping("/user/add")
    public User addUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
