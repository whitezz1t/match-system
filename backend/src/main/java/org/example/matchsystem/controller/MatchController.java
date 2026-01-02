package org.example.matchsystem.controller;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import org.example.matchsystem.entity.*;
import org.example.matchsystem.repository.MatchRepository;
import org.example.matchsystem.repository.MatchRoundRepository;
import org.example.matchsystem.repository.PlayerRepository;
import org.example.matchsystem.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchRoundRepository matchRoundRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    // 1. 开始比赛接口
    @PostMapping("/start")
    public Match startMatch(@RequestParam Integer playerAId,
                            @RequestParam Integer playerBId,
                            @RequestParam String level) {
        return matchService.startMatch(playerAId, playerBId, level);
    }

// 2. 查看比赛列表 (✨ 修改：支持筛选参数)
    // URL 示例: GET /api/matches?level=决赛&playerId=1&startDate=2023-01-01
    @GetMapping
    public List<Match> getAllMatches(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Integer playerId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(pattern="yyyy-MM-dd") Date endDate
    ) {
        // 如果没有传参数，这些变量就是 null，Service 会处理
        return matchService.searchMatches(level, playerId, startDate, endDate);
    }

    // 3. 记分接口
    @PostMapping("/{matchId}/score")
    public Match recordScore(@PathVariable Integer matchId,
                             @RequestParam Integer winnerId) {
        return matchService.recordScore(matchId, winnerId);
    }

    // 4. 获取比赛详情
    @GetMapping("/{matchId}/rounds")
    public List<MatchRound> getMatchRounds(@PathVariable Integer matchId) {
        return matchService.getMatchRounds(matchId);
    }

    // 5. 导出 Excel 接口 (✨ 终极合并版：名字解析 + 发球方计算)
    @GetMapping("/{matchId}/export")
    public void exportMatchData(@PathVariable Integer matchId, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("比赛记录_" + matchId, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 1. 获取所有回合
        List<MatchRound> rounds = matchRoundRepository.findByMatchId(matchId);

        // 2. 获取比赛信息
        Match match = matchRepository.findById(matchId).orElse(null);

        // 3. 预先查出两位选手的信息
        String playerAName = "未知选手A";
        String playerBName = "未知选手B";
        Integer playerAId = null;
        Integer playerBId = null;

        if (match != null) {
            playerAId = match.getPlayerAId();
            playerBId = match.getPlayerBId();

            if (playerAId != null) {
                Player pA = playerRepository.findById(playerAId).orElse(null);
                if (pA != null) playerAName = pA.getName();
            }
            if (playerBId != null) {
                Player pB = playerRepository.findById(playerBId).orElse(null);
                if (pB != null) playerBName = pB.getName();
            }
        }

        // 4. 组装导出数据
        List<MatchExportVO> exportData = new ArrayList<>();

        for (MatchRound r : rounds) {
            MatchExportVO vo = new MatchExportVO();
            vo.setSetNumber("第1局");
            vo.setRoundNumber(r.getRoundNumber());

            // ✨ A. 计算得分方名字 (你刚才写的逻辑)
            String winnerName = "";
            Integer wid = r.getWinnerId();
            if (wid != null) {
                if (wid.equals(playerAId)) {
                    winnerName = playerAName;
                } else if (wid.equals(playerBId)) {
                    winnerName = playerBName;
                } else {
                    winnerName = "未知ID:" + wid;
                }
            }
            vo.setWinnerName(winnerName);


            // ✨ B. 精准计算发球方 (把这段加回来了！)
            // 这一球结束后的总分
            int totalPointsAfter = (r.getScoreA() == null ? 0 : r.getScoreA()) + (r.getScoreB() == null ? 0 : r.getScoreB());
            // 这一球开始前的总分
            int totalPointsBefore = totalPointsAfter - 1;
            if (totalPointsBefore < 0) totalPointsBefore = 0;

            String serverName;
            // 规则：20分前每2球换发，20分后每1球换发
            if (totalPointsBefore < 20) {
                // 偶数轮A发，奇数轮B发
                if ((totalPointsBefore / 2) % 2 == 0) {
                    serverName = playerAName;
                } else {
                    serverName = playerBName;
                }
            } else {
                // 10平后轮流发
                if (totalPointsBefore % 2 == 0) {
                    serverName = playerAName;
                } else {
                    serverName = playerBName;
                }
            }
            vo.setServerName(serverName); // 设置计算出的发球人名字

            // ----------------------------------------

            vo.setScoreSnapshot(r.getScoreA() + " : " + r.getScoreB());

            if (r.getScoreTime() != null) {
                vo.setTime(java.sql.Timestamp.valueOf(r.getScoreTime()));
            } else {
                vo.setTime(new Date());
            }

            vo.setVideoFile("match_" + matchId + "_round_" + r.getRoundNumber() + ".mp4");
            exportData.add(vo);
        }

        EasyExcel.write(response.getOutputStream(), MatchExportVO.class)
                .sheet("比赛详情")
                .doWrite(exportData);
    }

    // 6. 删除比赛接口
    @DeleteMapping("/{matchId}")
    public void deleteMatch(@PathVariable Integer matchId) {
        matchService.deleteMatch(matchId);
    }

    // ... 其他接口 ...

    // 7. [新增] 获取高级统计数据
    @GetMapping("/{matchId}/stats")
    public MatchStatsVO getMatchStats(@PathVariable Integer matchId) {
        return matchService.calculateStats(matchId);
    }

    // 8. [新增] 上传视频接口
    @PostMapping("/{matchId}/rounds/{roundNumber}/video")
    public void uploadVideo(@PathVariable Integer matchId,
                            @PathVariable Integer roundNumber,
                            @RequestParam("file") MultipartFile file) {
        matchService.uploadVideo(matchId, roundNumber, file);
    }
}