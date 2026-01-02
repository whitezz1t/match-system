package org.example.matchsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date; // 引入 Date，为了兼容性

@Data
@Entity
@Table(name = "match_rounds")
public class MatchRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_id")
    private Integer roundId;

    @Column(name = "match_id")
    private Integer matchId;

    @Column(name = "set_number")
    private Integer setNumber;

    @Column(name = "round_number")
    private Integer roundNumber;

    @Column(name = "score_a")
    private Integer scoreA;

    @Column(name = "score_b")
    private Integer scoreB;

    @Column(name = "server_name")
    private String serverName;

    @Column(name = "scorer_name")
    private String scorerName;

    // ✅ 新增：为了解决 Controller 报错，必须加上这个字段
    @Column(name = "winner_id")
    private Integer winnerId;

    @Column(name = "score_time")
    private LocalDateTime scoreTime;

    @Column(name = "video_file_path")
    private String videoFilePath;

    // ==========================================
    // 手动 Getter / Setter 区域
    // ==========================================

    public Integer getRoundId() { return roundId; }
    public void setRoundId(Integer roundId) { this.roundId = roundId; }

    public Integer getMatchId() { return matchId; }
    public void setMatchId(Integer matchId) { this.matchId = matchId; }

    public Integer getSetNumber() { return setNumber; }
    public void setSetNumber(Integer setNumber) { this.setNumber = setNumber; }

    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }

    public Integer getScoreA() { return scoreA; }
    public void setScoreA(Integer scoreA) { this.scoreA = scoreA; }

    public Integer getScoreB() { return scoreB; }
    public void setScoreB(Integer scoreB) { this.scoreB = scoreB; }

    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }

    public String getScorerName() { return scorerName; }
    public void setScorerName(String scorerName) { this.scorerName = scorerName; }

    // ✅ 新增 Getter/Setter
    public Integer getWinnerId() { return winnerId; }
    public void setWinnerId(Integer winnerId) { this.winnerId = winnerId; }

    public LocalDateTime getScoreTime() { return scoreTime; }
    public void setScoreTime(LocalDateTime scoreTime) { this.scoreTime = scoreTime; }

    public String getVideoFilePath() { return videoFilePath; }
    public void setVideoFilePath(String videoFilePath) { this.videoFilePath = videoFilePath; }
}