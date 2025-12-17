package org.example.matchsystem.repository;

import org.example.matchsystem.entity.MatchRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRoundRepository extends JpaRepository<MatchRound, Integer> {
    // 这里我们可以神奇地定义一个方法，Spring 会自动帮我们实现功能：
    // 找出一场比赛(matchId)下的所有回合数据
    List<MatchRound> findByMatchId(Integer matchId);
}