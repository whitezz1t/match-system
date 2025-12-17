package org.example.matchsystem.controller;

import org.example.matchsystem.entity.Match;
import org.example.matchsystem.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    // 1. 开始比赛接口 (POST)
    // URL: http://localhost:8080/api/matches/start?playerAId=1&playerBId=2&level=决赛
    @PostMapping("/start")
    public Match startMatch(@RequestParam Integer playerAId,
                            @RequestParam Integer playerBId,
                            @RequestParam String level) {
        return matchService.startMatch(playerAId, playerBId, level);
    }

    // 2. 查看所有比赛 (GET)
    @GetMapping
    public List<Match> getAllMatches() {
        return matchService.getAllMatches();
    }

    // 3. 记分接口 (POST)
    // URL: http://localhost:8080/api/matches/1/score?winnerId=1
    // {matchId} 是路径参数，代表给哪场比赛记分
    @PostMapping("/{matchId}/score")
    public Match recordScore(@PathVariable Integer matchId,
                             @RequestParam Integer winnerId) {
        return matchService.recordScore(matchId, winnerId);
    }

    // 4. 获取比赛详情(所有回合)接口 (GET)
    @GetMapping("/{matchId}/rounds")
    public java.util.List<org.example.matchsystem.entity.MatchRound> getMatchRounds(@PathVariable Integer matchId) {
        return matchService.getMatchRounds(matchId);
    }
}