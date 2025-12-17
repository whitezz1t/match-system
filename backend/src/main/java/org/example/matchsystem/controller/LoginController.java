package org.example.matchsystem.controller;

import org.example.matchsystem.entity.User;
import org.example.matchsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    // 登录接口
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // 去数据库查有没有这个人
        User user = userRepository.findByUsernameAndPassword(username, password);

        if (user != null) {
            return "success"; // 查到了，返回成功
        } else {
            return "fail"; // 没查到，返回失败
        }
    }

    // 注册接口
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        // 1. 先检查用户名是不是已经被人注册了
        // 我们利用 JPA 的特性，虽然没定义 findByUsername，但可以尝试用 example 或者直接扩展 Repository
        // 这里为了简单，我们先去 UserRepository 加一个方法
        User existUser = userRepository.findByUsername(username);

        if (existUser != null) {
            return "exist"; // 用户名已存在
        }

        // 2. 如果没注册过，就保存新用户
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // 实际开发中密码要加密，这里先明文存
        userRepository.save(newUser);

        return "success";
    }
}