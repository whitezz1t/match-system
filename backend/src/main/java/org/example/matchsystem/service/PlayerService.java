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

    // 3. [新增] 删除选手
    public void deletePlayer(Integer id) {
        // 注意：如果该选手有比赛记录，数据库会抛出外键约束异常，Controller层或前端需要处理
        playerRepository.deleteById(id);
    }

    // 4. [新增] 修改选手信息
    public Player updatePlayer(Integer id, String newName) {
        // 先查出来
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("选手不存在"));
        // 修改名字
        player.setName(newName);
        // 保存（JPA 检测到有ID存在，会自动执行 Update）
        return playerRepository.save(player);
    }
}