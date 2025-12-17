package org.example.matchsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Integer matchId;

    @Column(name = "match_date")
    private LocalDateTime matchDate; // 使用 Java 8 的时间类型

    @Column(name = "level")
    private String level;

    @Column(name = "player_a_id")
    private Integer playerAId;

    @Column(name = "player_b_id")
    private Integer playerBId;

    @Column(name = "player_a_name")
    private String playerAName;

    @Column(name = "player_b_name")
    private String playerBName;

    @Column(name = "final_score_a")
    private Integer finalScoreA;

    @Column(name = "final_score_b")
    private Integer finalScoreB;

    @Column(name = "winner_id")
    private Integer winnerId;

    @Column(name = "status")
    private String status; // ONGOING, FINISHED

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getPlayerAId() {
        return playerAId;
    }

    public void setPlayerAId(Integer playerAId) {
        this.playerAId = playerAId;
    }

    public Integer getPlayerBId() {
        return playerBId;
    }

    public void setPlayerBId(Integer playerBId) {
        this.playerBId = playerBId;
    }

    public String getPlayerAName() {
        return playerAName;
    }

    public void setPlayerAName(String playerAName) {
        this.playerAName = playerAName;
    }

    public String getPlayerBName() {
        return playerBName;
    }

    public void setPlayerBName(String playerBName) {
        this.playerBName = playerBName;
    }

    public Integer getFinalScoreA() {
        return finalScoreA;
    }

    public void setFinalScoreA(Integer finalScoreA) {
        this.finalScoreA = finalScoreA;
    }

    public Integer getFinalScoreB() {
        return finalScoreB;
    }

    public void setFinalScoreB(Integer finalScoreB) {
        this.finalScoreB = finalScoreB;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}