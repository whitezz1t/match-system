package org.example.matchsystem.service;

import org.example.matchsystem.entity.Match;
import org.example.matchsystem.entity.MatchRound;
import org.example.matchsystem.entity.MatchStatsVO;
import org.example.matchsystem.entity.Player;
import org.example.matchsystem.repository.MatchRepository;
import org.example.matchsystem.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    // 3. 记分逻辑 (包含 11 分制裁判规则)
    public Match recordScore(Integer matchId, Integer winnerId) {
        Match match = matchRepository.findById(matchId).orElseThrow();

        // 🛑 裁判拦截：如果比赛已经结束，禁止继续加分
        if ("FINISHED".equals(match.getStatus())) {
            throw new RuntimeException("比赛已结束，无法继续记分！");
        }

        // 1. 更新大比分
        if (winnerId.equals(match.getPlayerAId())) {
            match.setFinalScoreA(match.getFinalScoreA() + 1);
        } else {
            match.setFinalScoreB(match.getFinalScoreB() + 1);
        }

        // 2. ⚖️ 裁判判断：11分制规则
        // 规则：任意一方 >= 11分，并且 领先对方 >= 2分
        int scoreA = match.getFinalScoreA();
        int scoreB = match.getFinalScoreB();

        if ((scoreA >= 11 || scoreB >= 11) && Math.abs(scoreA - scoreB) >= 2) {
            match.setStatus("FINISHED"); // 更改状态为已结束
            // 可以在这里设置获胜者ID，如果你有这个字段的话
        } else {
            match.setStatus("ONGOING");
        }

        matchRepository.save(match);

        // 3. 记录由于是哪个回合
        MatchRound round = new MatchRound();
        round.setMatchId(matchId);
        round.setRoundNumber(getMatchRounds(matchId).size() + 1);
        round.setScoreA(match.getFinalScoreA());
        round.setScoreB(match.getFinalScoreB());
        round.setWinnerId(winnerId); // 记录这一球谁赢的
        round.setScoreTime(java.time.LocalDateTime.now()); // ✨ 记录当前发生的准确时间
        matchRoundRepository.save(round);

        return match;
    }

    // 5. 删除比赛 (级联删除)
    @Transactional // 👈 加上这个，保证要么全删，要么全不删
    public void deleteMatch(Integer matchId) {
        // 第一步：先删除这张表里的回合记录 (查出来 -> 删掉)
        List<MatchRound> rounds = matchRoundRepository.findByMatchId(matchId);
        matchRoundRepository.deleteAll(rounds);

        // 第二步：再删除比赛本身
        matchRepository.deleteById(matchId);
    }
    // 功能：查询某场比赛的所有回合细节
    public java.util.List<org.example.matchsystem.entity.MatchRound> getMatchRounds(Integer matchId) {
        return matchRoundRepository.findByMatchId(matchId);
    }

    // 功能：获取所有比赛列表
    public java.util.List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    // ... 原有的代码 ...

    // 7. [新增] 计算比赛统计数据
    public MatchStatsVO calculateStats(Integer matchId) {
        List<MatchRound> rounds = matchRoundRepository.findByMatchId(matchId);
        Match match = matchRepository.findById(matchId).orElseThrow();

        MatchStatsVO stats = new MatchStatsVO();

        if (rounds.isEmpty()) {
            return stats; // 如果没数据，返回空对象
        }

        // --- 变量初始化 ---
        int totalServesA = 0;
        int winOnServeA = 0;
        int totalServesB = 0;
        int winOnServeB = 0;

        int currentStreakA = 0;
        int maxStreakA = 0;
        int currentStreakB = 0;
        int maxStreakB = 0;

        long totalDurationMillis = 0;
        int durationCount = 0;

        // --- 遍历每一回合 ---
        for (int i = 0; i < rounds.size(); i++) {
            MatchRound r = rounds.get(i);

            // 1. 计算连胜 (Streak)
            if (r.getWinnerId().equals(match.getPlayerAId())) {
                currentStreakA++;
                currentStreakB = 0;
            } else {
                currentStreakB++;
                currentStreakA = 0;
            }
            maxStreakA = Math.max(maxStreakA, currentStreakA);
            maxStreakB = Math.max(maxStreakB, currentStreakB);

            // 2. 计算发球得分率 (利用之前的推算逻辑)
            // 这一球结束后的总分是 (ScoreA + ScoreB)
            // 这一球开始前的总分是 (ScoreA + ScoreB - 1)
            int scoreA = r.getScoreA() != null ? r.getScoreA() : 0;
            int scoreB = r.getScoreB() != null ? r.getScoreB() : 0;
            int totalPointsBefore = (scoreA + scoreB) - 1;
            if (totalPointsBefore < 0) totalPointsBefore = 0;

            boolean isAServing;
            if (totalPointsBefore < 20) {
                isAServing = (totalPointsBefore / 2) % 2 == 0;
            } else {
                isAServing = totalPointsBefore % 2 == 0;
            }

            if (isAServing) {
                totalServesA++;
                if (r.getWinnerId().equals(match.getPlayerAId())) winOnServeA++;
            } else {
                totalServesB++;
                if (r.getWinnerId().equals(match.getPlayerBId())) winOnServeB++;
            }

            // 3. 计算时间间隔 (当前回合时间 - 上一回合时间)
            if (i > 0 && r.getScoreTime() != null && rounds.get(i-1).getScoreTime() != null) {
                Duration duration = Duration.between(rounds.get(i-1).getScoreTime(), r.getScoreTime());
                // 过滤掉异常数据（比如间隔超过3分钟可能是暂停，不计入平均）
                if (duration.getSeconds() > 0 && duration.getSeconds() < 180) {
                    totalDurationMillis += duration.toMillis();
                    durationCount++;
                }
            }
        }

        // --- 结果封装 ---
        stats.setMaxStreakA(maxStreakA);
        stats.setMaxStreakB(maxStreakB);

        stats.setServeWinRateA(totalServesA == 0 ? 0.0 : (double) winOnServeA / totalServesA);
        stats.setServeWinRateB(totalServesB == 0 ? 0.0 : (double) winOnServeB / totalServesB);

        stats.setAvgDurationSeconds(durationCount == 0 ? 0.0 : (double) totalDurationMillis / durationCount / 1000.0);

        return stats;
    }

    // 8. [新增] 带筛选的查询
    public List<Match> searchMatches(String level, Integer playerId, Date startDate, Date endDate) {
        // 如果所有条件都为空，直接查所有 (虽然 Repository 逻辑也能处理，但这样省一点点资源)
        if (level == null && playerId == null && startDate == null && endDate == null) {
            return matchRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "matchDate"));
        }
        return matchRepository.findMatchesWithFilter(level, playerId, startDate, endDate);
    }

    // 9. [新增] 上传回合视频
    public void uploadVideo(Integer matchId, Integer roundNumber, MultipartFile file) {
        try {
            // 1. 构造文件名: match_{id}_round_{num}.webm
            String fileName = "match_" + matchId + "_round_" + roundNumber + ".webm";

            // 2. 构造本地保存路径
            String savePath = System.getProperty("user.dir") + File.separator + "videos" + File.separator + fileName;

            // 3. 保存文件
            file.transferTo(new File(savePath));

            // 4. 更新数据库中的路径 (这里我们存 Web 访问路径)
            // 也就是 /videos/match_1_round_1.webm
            MatchRound round = matchRoundRepository.findByMatchId(matchId).stream()
                    .filter(r -> r.getRoundNumber().equals(roundNumber))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("回合不存在"));

            round.setVideoFilePath("/videos/" + fileName);
            matchRoundRepository.save(round);

        } catch (IOException e) {
            throw new RuntimeException("视频上传失败: " + e.getMessage());
        }
    }
}