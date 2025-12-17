package org.example.matchsystem.service;

import org.example.matchsystem.entity.Player;
import org.example.matchsystem.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // 告诉 Spring 这是一个“经理”
public class PlayerService {

    @Autowired // 自动把“管家”请过来
    private PlayerRepository playerRepository;

    // 功能 1：添加新球员
    public Player addPlayer(String name) {
        Player player = new Player();
        player.setName(name);
        // 让管家保存到数据库
        return playerRepository.save(player);
    }

    // 功能 2：查询所有球员
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
}