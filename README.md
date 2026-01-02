没问题！我参照你提供的“校园失物招领系统”的精美格式，结合我们刚刚完成的 **SpringBoot + Vue + 视频回放** 乒乓球系统的技术细节，为你重新编写了这份 `README.md`。

你可以直接复制以下内容到你的 `README.md` 文件中。

---

# 🏓 乒乓球比赛智能管理与回放系统 V1.0

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0+-green.svg)
![Vue](https://img.shields.io/badge/Vue.js-3.0-42b883.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ed.svg)

**集智能记分、鹰眼回放、数据分析于一体的现代化赛事管理平台**

[功能特性](#-功能特性) • [快速开始](#-快速开始) • [系统架构](#-系统架构) • [API 文档](#-api-文档) • [部署指南](#-部署指南)

</div>

---

## ✨ 功能特性

### ⏱️ 智能裁判引擎
- **规则自动化**：内置乒乓球国际规则，自动判断发球权轮换（2球/1球）、追分（Deuce）逻辑及胜负判定。
- **状态管理**：支持比赛暂停、中断恢复，实时显示当前发球方与局点提示。
- **防误触机制**：记分操作具备“冷却锁”功能，防止连点导致数据错误。

### 📹 鹰眼视频回放
- **自动切片录制**：基于 `MediaRecorder` 的切片录制技术，自动记录“上一分结束”至“本回合得分后2秒”的完整片段。
- **零人工干预**：裁判仅需点击加分，系统后台自动完成视频截取、上传与存储。
- **即时回看**：支持比赛过程中随时点击历史回合，弹窗回放精彩得分瞬间。
- **多设备支持**：支持调用系统默认摄像头或 USB 外接高清采集卡。

### 📊 数据可视化分析
- **比分走势图**：实时绘制 ECharts 折线图，复盘比赛胶着程度。
- **战术统计**：自动计算发球得分率、最大连胜回数（Streak）、平均回合时长。
- **历史归档**：完整保存每场比赛的逐球记录与视频档案，支持 Excel 导出。

### 🐳 全栈容器化
- **一键部署**：前后端与数据库完全容器化，通过 Docker Compose 一键启动。
- **数据持久化**：视频文件与数据库数据挂载至宿主机，防止数据丢失。

---

## 🚀 快速开始

### 环境要求

- Java 17+
- Node.js 16+ (用于前端开发)
- Docker & Docker Compose (推荐)
- 摄像头 (笔记本自带或外接 USB)

### 方式一：Docker 一键部署 (推荐)


# 1. 克隆项目
git clone [https://github.com/你的用户名/pingpong-match-system.git](https://github.com/你的用户名/pingpong-match-system.git)
cd pingpong-match-system

# 2. 启动服务 (自动构建镜像 + 启动 MySQL)
docker compose up -d

# 3. 查看日志
docker compose logs -f match-app

```

> **注意**：首次启动需等待 MySQL 初始化及 Maven 依赖下载，约需 2-5 分钟。

### 方式二：本地开发运行

**1. 后端 (Spring Boot)**

```bash
cd backend
# 修改 application.properties 中的数据库配置
mvn spring-boot:run

```

**2. 前端 (Vue 3)**

```bash
cd frontend
npm install
npm run dev

```

### 访问地址

| 服务 | 地址 |
| --- | --- |
| 🌐 Web 记分牌 | http://localhost:8080 |
| 📹 视频存储路径 | 默认为项目根目录下的 `/videos` |

---

## 📐 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端 (Vue 3 + Vite)                   │
├───────────────┬───────────────┬───────────────┬─────────────┤
│   Element UI  │    ECharts    │ MediaRecorder │    Axios    │
├───────────────┴───────────────┴───────────────┴─────────────┤
│                            HTTP / REST API                  │
├─────────────────────────────────────────────────────────────┤
│                     后端 (Spring Boot 3)                     │
├───────────────┬───────────────┬───────────────┬─────────────┤
│  Rule Engine  │  Video SVC    │   Match SVC   │  File I/O   │
│ (规则判定引擎)  │ (视频流处理)   │ (赛事管理)     │ (静态映射)   │
├───────────────┴───────────────┴───────────────┴─────────────┤
│                        基础设施层                            │
├───────────────────────────────┬─────────────────────────────┤
│          MySQL 8.0            │       Local File System     │
│       (结构化数据存储)          │        (.webm 视频存储)      │
└───────────────────────────────┴─────────────────────────────┘

```

### 技术栈

| 层级 | 技术 | 说明 |
| --- | --- | --- |
| **前端** | Vue 3, Vite | 响应式构建 |
| **UI库** | Element Plus | 界面组件 |
| **图表** | Apache ECharts | 数据可视化 |
| **多媒体** | HTML5 Media API | 视频流捕获与切片 |
| **后端** | Spring Boot 3 | 核心业务逻辑 |
| **ORM** | Spring Data JPA | 数据库交互 |
| **数据库** | MySQL 8.0 | 数据持久化 |
| **部署** | Docker Compose | 容器编排 |

---

## 📖 API 文档

### 核心接口说明

#### 赛事管理

| 方法 | 路径 | 描述 |
| --- | --- | --- |
| POST | `/api/matches/start` | 创建一场新比赛 |
| GET | `/api/matches` | 获取比赛列表（支持筛选） |
| GET | `/api/matches/{id}` | 获取比赛详情 |

#### 记分与视频

| 方法 | 路径 | 描述 |
| --- | --- | --- |
| POST | `/api/matches/{id}/score` | 记录得分 (触发规则引擎) |
| POST | `/api/matches/{id}/rounds/{round}/video` | 上传该回合的关键帧视频 |
| GET | `/api/matches/{id}/rounds` | 获取该场比赛的所有回合记录 |

#### 统计分析

| 方法 | 路径 | 描述 |
| --- | --- | --- |
| GET | `/api/matches/{id}/stats` | 获取发球胜率、连胜统计等 |

---

## 🐳 部署配置

### Docker 环境变量 (.env)

你可以创建 `.env` 文件来覆盖默认配置：

```ini
# 数据库配置
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=match_db

# 应用配置
# 如果使用 USB 采集卡，建议在浏览器端选择设备，此处无需配置

```

### 挂载卷说明

在 `compose.yml` 中，我们定义了以下挂载：

* `./videos:/app/videos`: 将容器内录制的视频同步保存到宿主机，确保容器删除后视频不丢失。
* `./init.sql:/docker-entrypoint-initdb.d/init.sql`: 容器首次启动时自动初始化的 SQL 脚本。

---

## 📁 项目结构

```
pingpong-match-system/
├── backend/            # Spring Boot 后端源码
│   ├── src/main/java/com/example/match
│   └── src/main/resources
├── frontend/           # Vue 3 前端源码
│   ├── src/components/Scoreboard.vue  # 核心记分组件
│   └── src/views/MatchList.vue
├── videos/             # [自动生成] 存放录制的比赛视频
├── compose.yml         # Docker 一键启动配置
├── Dockerfile          # 后端镜像构建文件
├── init.sql            # 数据库初始化脚本
└── README.md           # 项目文档

```

---

## 🔧 常见问题

**Q: 视频录制只有声音没有画面？**
A: 请确保浏览器已授予摄像头权限。如果是笔记本连接外接采集卡，请在页面顶部的下拉框中切换摄像头设备。

**Q: 视频无法播放或显示文件损坏？**
A: 系统采用了“单回合单文件”的切片策略，请确保不要在2秒内连续点击得分，系统需要时间生成关键帧头文件。

---

## 📄 许可证

MIT License © 2026

---

<div align="center">

**Ping Pong Match System** | Made with ❤️ by YourName

</div>

```

```