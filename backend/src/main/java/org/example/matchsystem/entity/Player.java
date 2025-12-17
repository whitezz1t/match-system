package org.example.matchsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Data;

@Data // Lombok 注解：自动帮我们生成 getter, setter, toString 等方法
@Entity // 告诉 JPA 这是一个跟数据库对应的实体类
@Table(name = "players") // 对应数据库里的 players 表
public class Player {


    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增 ID
    @Column(name = "player_id")
    private Integer playerId;

    @Column(name = "name")
    private String name;

    @Column(name = "avatar")
    private String avatar;

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}