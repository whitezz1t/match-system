package org.example.matchsystem.service;

import org.example.matchsystem.entity.Match;
import org.example.matchsystem.entity.Player;
import org.example.matchsystem.repository.MatchRepository;
import org.example.matchsystem.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    // 功能：开始一场新比赛
    public Match startMatch(Integer playerAId, Integer playerBId, String level) {
        // 1. 确认两名球员都存在
        Player playerA = playerRepository.findById(playerAId).orElseThrow(() -> new RuntimeException("球员A不存在"));
        Player playerB = playerRepository.findById(playerBId).orElseThrow(() -> new RuntimeException("球员B不存在"));

        // 2. 创建比赛对象
        Match match = new Match();
        match.setPlayerAId(playerAId);
        match.setPlayerBId(playerBId);
        match.setPlayerAName(playerA.getName()); // 把名字也存进去，方便查询
        match.setPlayerBName(playerB.getName());
        match.setLevel(level);
        match.setMatchDate(LocalDateTime.now()); // 设置当前时间
        match.setFinalScoreA(0); // 初始比分 0
        match.setFinalScoreB(0);
        match.setStatus("ONGOING"); // 状态：进行中

        // 3. 保存到数据库
        return matchRepository.save(match);
    }

    @Autowired
    private org.example.matchsystem.repository.MatchRoundRepository matchRoundRepository; // 记得注入这个新的管家

    // 功能：记录得分
    public Match recordScore(Integer matchId, Integer winnerId) {
        // 1. 找到这场比赛
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));

        // 2. 判断是谁得分，更新比赛的总分
        // 注意：这里我们简化逻辑，直接在 finalScore 上加分。
        // 如果是正规网球/羽毛球规则，需要在这里写判定(比如满11分赢一局)，以后可以扩展。
        if (winnerId.equals(match.getPlayerAId())) {
            match.setFinalScoreA(match.getFinalScoreA() + 1);
        } else if (winnerId.equals(match.getPlayerBId())) {
            match.setFinalScoreB(match.getFinalScoreB() + 1);
        } else {
            throw new RuntimeException("得分选手不在本场比赛中");
        }

        // 3. 记录这一回合的详细信息 (为了以后做回放和图表)
        org.example.matchsystem.entity.MatchRound round = new org.example.matchsystem.entity.MatchRound();
        round.setMatchId(matchId);
        round.setScoreA(match.getFinalScoreA()); // 记录当前比分
        round.setScoreB(match.getFinalScoreB());
        round.setScorerName(winnerId.equals(match.getPlayerAId()) ? match.getPlayerAName() : match.getPlayerBName());
        round.setScoreTime(LocalDateTime.now()); // 记录时间
        round.setSetNumber(1); // 暂时默认为第1局
        round.setRoundNumber(match.getFinalScoreA() + match.getFinalScoreB()); // 回合数 = 总分之和

        // 4. 保存数据
        matchRoundRepository.save(round); // 存回合记录
        return matchRepository.save(match); // 存最新比分
    }

    // 功能：查询某场比赛的所有回合细节
    public java.util.List<org.example.matchsystem.entity.MatchRound> getMatchRounds(Integer matchId) {
        return matchRoundRepository.findByMatchId(matchId);
    }

    // 功能：获取所有比赛列表
    public java.util.List<Match> getAllMatches() {
        return matchRepository.findAll();
    }
}