package org.example.matchsystem.repository;

import org.example.matchsystem.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    // 继承 JpaRepository 后，我们就自动拥有了 save(保存), findAll(查询所有), delete(删除) 等功能
    // Integer 代表 Player 表主键(player_id) 的类型
}