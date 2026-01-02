package org.example.matchsystem.entity;

import lombok.Data;

@Data
public class MatchStatsVO {
    // 1. 发球得分率 (0.0 - 1.0)
    private Double serveWinRateA; // 选手A发球时的胜率
    private Double serveWinRateB; // 选手B发球时的胜率

    // 2. 最大连续得分 (连胜)
    private Integer maxStreakA;
    private Integer maxStreakB;

    // 3. 回合平均耗时 (秒)
    private Double avgDurationSeconds;

    public Double getServeWinRateA() {
        return serveWinRateA;
    }

    public void setServeWinRateA(Double serveWinRateA) {
        this.serveWinRateA = serveWinRateA;
    }

    public Double getServeWinRateB() {
        return serveWinRateB;
    }

    public void setServeWinRateB(Double serveWinRateB) {
        this.serveWinRateB = serveWinRateB;
    }

    public Integer getMaxStreakA() {
        return maxStreakA;
    }

    public void setMaxStreakA(Integer maxStreakA) {
        this.maxStreakA = maxStreakA;
    }

    public Integer getMaxStreakB() {
        return maxStreakB;
    }

    public void setMaxStreakB(Integer maxStreakB) {
        this.maxStreakB = maxStreakB;
    }

    public Double getAvgDurationSeconds() {
        return avgDurationSeconds;
    }

    public void setAvgDurationSeconds(Double avgDurationSeconds) {
        this.avgDurationSeconds = avgDurationSeconds;
    }
}