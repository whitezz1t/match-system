package org.example.matchsystem.controller;

import org.example.matchsystem.entity.Player;
import org.example.matchsystem.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 告诉 Spring 这是一个“前台”，专门处理网络请求
@RequestMapping("/api/players") // 以后访问 http://localhost:8080/api/players 就到这里
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    // 1. 添加球员接口 (POST 请求)
    // 只有在浏览器地址栏输入是不行的，需要用工具发送 POST 请求
    @PostMapping
    public Player addPlayer(@RequestParam String name) {
        return playerService.addPlayer(name);
    }

    // 2. 获取所有球员接口 (GET 请求)
    // 这个可以直接在浏览器里打开看！
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }


    // 3. [新增] 删除选手接口 (DELETE)
    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable Integer id) {
        playerService.deletePlayer(id);
    }

    // 4. [新增] 修改选手接口 (PUT)
    @PutMapping("/{id}")
    public Player updatePlayer(@PathVariable Integer id, @RequestParam String name) {
        return playerService.updatePlayer(id, name);
    }
}