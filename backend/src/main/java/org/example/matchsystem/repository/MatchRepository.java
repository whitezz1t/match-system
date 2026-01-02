package org.example.matchsystem.repository;

import org.example.matchsystem.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    // ✨ 新增：动态筛选查询
    // 逻辑：如果参数是 null，就忽略该条件；否则匹配该条件
    // 对于选手：只要 A 是他 或者 B 是他，都算搜到
    @Query("SELECT m FROM Match m WHERE " +
            "(:level IS NULL OR m.level = :level) AND " +
            "(:playerId IS NULL OR (m.playerAId = :playerId OR m.playerBId = :playerId)) AND " +
            "(:startDate IS NULL OR m.matchDate >= :startDate) AND " +
            "(:endDate IS NULL OR m.matchDate <= :endDate) " +
            "ORDER BY m.matchDate DESC")
    List<Match> findMatchesWithFilter(@Param("level") String level,
                                      @Param("playerId") Integer playerId,
                                      @Param("startDate") Date startDate,
                                      @Param("endDate") Date endDate);
}