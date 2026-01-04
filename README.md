这份 README 已经很不错了，但根据我们刚才解决的一系列深度技术问题（特别是 Docker 环境下的**架构变更**、**硬件延迟修复**、**时区问题**和**静态资源映射**），我们需要对文档进行关键性的更新。

以下是优化后的版本。主要变化包括：

1. **架构图更新**：反映了“Vue + Spring Boot 合并部署”的单容器架构。
2. **部署指南增强**：增加了清理旧容器和处理数据库缓存的步骤（这是你遇到的最大坑）。
3. **配置说明**：补充了时区（TZ）和端口映射的说明。
4. **常见问题**：加入了针对 Docker on Windows 硬件延迟的解释。

---

# 🏓 乒乓球比赛智能管理与回放系统 V1.0

<div align="center">

**集智能记分、鹰眼回放、数据分析于一体的现代化赛事管理平台**





*专为 Docker 环境优化，解决硬件录制延迟与跨平台部署难题*

[功能特性](https://www.google.com/search?q=%23-%E5%8A%9F%E8%83%BD%E7%89%B9%E6%80%A7) • [系统架构](https://www.google.com/search?q=%23-%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84) • [快速开始](https://www.google.com/search?q=%23-%E5%BF%AB%E9%80%9F%E5%BC%80%E5%A7%8B) • [部署指南](https://www.google.com/search?q=%23-%E9%83%A8%E7%BD%B2%E6%8C%87%E5%8D%97) • [常见问题](https://www.google.com/search?q=%23-%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)

</div>

---

## ✨ 功能特性

### ⏱️ 智能裁判引擎

* **规则自动化**：内置乒乓球国际规则，自动判断发球权轮换（2球/1球）、追分（Deuce）及胜负。
* **状态管理**：支持比赛暂停、中断恢复，防误触“冷却锁”机制。

### 📹 鹰眼视频回放 (Docker 增强版)

* **智能切片**：自动截取“得分前后”的高光时刻，生成独立 WebM/MP4 文件。
* **硬件防抖动**：针对 Docker on Windows/Linux 的硬件资源释放延迟，内置智能排队与延时机制，确保连续得分录制不丢包。
* **即时回看**：前端通过静态资源映射直接回放录像，无需额外的文件服务器。

### 📊 数据可视化与导出

* **图表分析**：集成 ECharts 比分走势图。
* **Excel 导出**：自动修正 Docker 容器时区偏差（UTC -> CST），确保导出的比赛时间准确无误。

### 🐳 极简单容器架构

* **合并部署**：彻底抛弃 Nginx，前端构建产物（Dist）直接嵌入 Spring Boot，解决 CORS 跨域问题。
* **零配置启动**：仅需两个容器（App + DB）即可运行完整系统。

---

## 📐 系统架构

本系统采用 **Spring Boot + Vue 合并部署** 策略，简化了运维复杂度：

```text
Host (宿主机 Windows/Linux)
│
├─ [Docker Container: match-app] (Port: 8080) ──────────────┐
│  │                                                        │
│  ├─ [Web Layer] Spring Boot Tomcat                        │
│  │   ├─ / (Root) -> 转发 index.html (Vue SPA)             │
│  │   ├─ /api/** -> 业务接口 REST API                     │
│  │   └─ /videos/** -> 映射宿主机挂载的视频目录             │
│  │                                                        │
│  ├─ [Service Layer] 业务逻辑 & FFmpeg 调用控制             │
│  └─ [Resource] 字体库 (FontConfig) & 静态网页              │
│                                                        │
└──────────────────────────┬────────────────────────────────┘
                           │ JDBC (Port: 3306)
                           ▼
   [Docker Container: match-db] (MySQL 8.0)

```

---

## 🚀 快速开始

### 环境要求

* Docker & Docker Compose
* 摄像头 (USB外接或内置)
* **注意**：本项目已配置 `TZ=Asia/Shanghai`，无需手动调整宿主机时区。

### ✅ 生产环境部署 (Docker Compose)

这是最推荐的方式，直接运行即可。

```bash
# 1. 克隆项目
git clone https://github.com/YourUsername/pingpong-match-system.git
cd pingpong-match-system

# 2. (可选) 如果之前运行过旧版本，建议先清理环境，防止端口/命名冲突
docker rm -f match-app match-db match-web

# 3. 一键构建并启动
docker compose up -d --build

# 4. 查看运行状态
docker ps
# 应只显示 match-app (8080端口) 和 match-db (3307端口)

```

### 访问地址

| 服务 | 地址 | 说明 |
| --- | --- | --- |
| 🌐 **Web 系统** | `http://localhost:8080` | 记分与回放主界面 |
| 🗄️ **数据库** | `localhost:3307` | 用户: root / 密码: root |

---

## 📁 目录与挂载说明

| 宿主机路径 | 容器内路径 | 说明 |
| --- | --- | --- |
| `./videos` | `/app/videos` | **视频存档**：自动同步，删除容器后视频不丢失 |
| `./mysql_data` | `/var/lib/mysql` | **数据库文件**：持久化存储，若需重置数据库请删除此文件夹 |
| `./logs` | `/app/logs` | **运行日志**：排查 FFmpeg 报错或系统异常 |

---

## 🔧 常见问题 (Troubleshooting)

### Q1: 为什么连续得分时，中间几回合的视频没录上？

**A:** 这是 Docker 在 Windows/Linux 上对 USB 设备的释放延迟导致的。
**解决方案**：系统已在 `MatchService` 中内置了 `1.5秒` 的强制等待逻辑。请确保不要修改代码中的 `Thread.sleep(1500)`，这是为了等待硬件释放文件锁。

### Q2: 网页能看到视频文件存在，但点击回放报 404？

**A:** 这是一个路径映射问题。
**解决方案**：本项目使用了 `WebConfig` 自动识别环境。

* 在 Docker 中，它会自动映射 `file:/app/videos/`。
* 请检查数据库 `video_file_path` 字段，确保存储的是**纯文件名** (如 `round_1.mp4`) 而不是绝对路径。

### Q3: 导出的 Excel 时间慢了 8 小时？

**A:** Docker 容器默认使用 UTC 时间。
**解决方案**：`compose.yml` 已配置 `TZ=Asia/Shanghai` 环境变量，重启容器后导出的时间即为北京时间。

### Q4: 数据库修改了 init.sql 但不生效？

**A:** Docker 只有在数据目录为空时才会执行初始化脚本。
**解决方案**：

1. `docker compose down`
2. **手动删除根目录下的 `mysql_data` 文件夹**
3. `docker compose up -d`

---

## 📄 许可证

MIT License © 2026