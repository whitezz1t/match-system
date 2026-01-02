package org.example.matchsystem.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import java.util.Date;

@ColumnWidth(20)
public class MatchExportVO {

    @ExcelProperty("局数")
    private String setNumber;

    @ExcelProperty("回合编号")
    private Integer roundNumber;

    @ExcelProperty("得分方")
    private String winnerName;

    @ExcelProperty("当前比分")
    private String scoreSnapshot;

    @ExcelProperty("发球方")
    private String serverName;

    @ExcelProperty("得分时间")
    private Date time;

    @ExcelProperty("视频片段文件名")
    private String videoFile;

    // ==========================================
    // 👇 手动补充所有的 Getter 和 Setter
    // ==========================================

    public String getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getScoreSnapshot() {
        return scoreSnapshot;
    }

    public void setScoreSnapshot(String scoreSnapshot) {
        this.scoreSnapshot = scoreSnapshot;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }
}