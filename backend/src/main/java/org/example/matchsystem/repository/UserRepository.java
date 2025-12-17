package org.example.matchsystem.repository;

import org.example.matchsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    // 自动生成查询方法：根据用户名和密码找用户
    User findByUsernameAndPassword(String username, String password);

    // 新加的：只查用户名，用于注册查重
    User findByUsername(String username);
}