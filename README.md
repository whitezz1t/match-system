

# 🏓 乒乓球比赛智能管理与回放系统 V1.0

<div align="center">

![Java](https://img.shields.io/badge/Java-22-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0+-green.svg)
![Vue](https://img.shields.io/badge/Vue.js-3.0-42b883.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Single_Container-2496ed.svg)

**集智能记分、鹰眼回放、数据分析于一体的现代化赛事管理平台**
<br>
*专为 Docker 环境优化，解决硬件录制延迟与跨平台部署难题*

[功能特性](#-功能特性) • [快速开始](#-快速开始) • [系统架构](#-系统架构) • [API 文档](#-api-文档) • [部署指南](#-部署指南) • [常见问题](#-常见问题)

</div>

---

## ✨ 功能特性

### ⏱️ 智能裁判引擎
- **规则自动化**：内置乒乓球国际规则，自动判断发球权轮换（2球/1球）、追分（Deuce）逻辑及胜负判定。
- **状态管理**：支持比赛暂停、中断恢复，实时显示当前发球方与局点提示。
- **防误触机制**：记分操作具备“冷却锁”功能，防止连点导致数据错误。

### 📹 鹰眼视频回放 (Docker 增强版)
- **自动切片录制**：基于 `MediaRecorder` 与 `FFmpeg` 的切片录制技术，自动记录“上一分结束”至“本回合得分后2秒”的完整片段。
- **硬件防抖动**：针对 Docker on Windows/Linux 的硬件资源释放延迟，内置智能排队与延时机制 (1.5s)，确保连续得分录制不丢失。
- **即时回看**：前端通过静态资源映射直接回放录像，无需额外的文件服务器。
- **多设备支持**：支持调用系统默认摄像头或 USB 外接高清采集卡。

### 📊 数据可视化分析
- **比分走势图**：实时绘制 ECharts 折线图，复盘比赛胶着程度。
- **战术统计**：自动计算发球得分率、最大连胜回数（Streak）、平均回合时长。
- **历史归档**：完整保存每场比赛的逐球记录与视频档案，支持 Excel 导出（自动修正容器时区偏差）。

### 🐳 极简单容器架构
- **合并部署**：前端构建产物（Dist）直接嵌入 Spring Boot，解决 CORS 跨域问题。
- **零配置启动**：仅需两个容器（App + DB）即可运行完整系统，彻底抛弃 Nginx 依赖。

---

## 🚀 快速开始

### 环境要求

- **Java 22+**
- Node.js 16+ (用于前端开发)
- Docker & Docker Compose (推荐)
- 摄像头 (笔记本自带或外接 USB)

### 方式一：Docker 一键部署 (生产环境推荐)

```bash
# 1. 克隆项目
git clone [https://github.com/whitezz1t/match-system](https://github.com/whitezz1t/match-system)
cd match-system

# 2. (可选) 清理旧环境，防止端口/命名冲突
docker rm -f match-app match-db match-web

# 3. 启动服务 (自动构建镜像 + 启动 MySQL)
docker compose up -d --build

# 4. 查看日志
docker compose logs -f match-app

```

> **注意**：首次启动需等待 MySQL 初始化及 Maven 依赖下载，约需 2-5 分钟。

### 方式二：本地开发运行 (前后端分离模式)

**0. 前置准备**
确保本地已安装 MySQL 8.0 并创建 `match_db` 数据库，或者单独启动 Docker 数据库：

```bash
docker compose up -d db

```

**1. 后端 (Spring Boot)**

```bash
cd backend
# ⚠️ 确保 src/main/resources/application.properties 指向你的本地数据库
# 默认运行端口: 8080
mvn spring-boot:run

```

**2. 前端 (Vue 3)**

```bash
cd frontend
npm install
# 默认运行端口: 5173 (Vite)
npm run dev

```

### 访问地址对照表

| 模式 | 服务 | 地址 | 说明 |
| --- | --- | --- | --- |
| **Docker 部署** | 🌐 完整系统 | `http://localhost:8080` | 前端页面与后端接口统一端口 |
|  | 🗄️ 数据库 | `localhost:3307` | 宿主机映射端口 |
| **本地开发** | 🌐 前端页面 | `http://localhost:5173` | Vite 开发服务器 |
|  | 🔌 后端接口 | `http://localhost:8080` | Spring Boot |

---



### 技术栈

| 层级 | 技术 | 说明 |
| --- | --- | --- |
| **前端** | Vue 3, Vite | 响应式构建 |
| **UI库** | Element Plus | 界面组件 |
| **图表** | Apache ECharts | 数据可视化 |
| **多媒体** | HTML5 Media API | 视频流捕获与切片 |
| **后端** | Spring Boot 3 | 核心业务逻辑 |
| **运行时** | **JDK 22** | 高性能 Java 环境 |
| **ORM** | Spring Data JPA | 数据库交互 |
| **数据库** | MySQL 8.0 | 数据持久化 |
| **部署** | Docker Compose | 单容器全栈编排 |

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

### 环境变量与挂载

在 `compose.yml` 中，我们要特别注意以下配置：

* **`TZ=Asia/Shanghai`**: 强制容器使用北京时间，解决导出的 Excel 时间偏差 8 小时的问题。
* **`./videos:/app/videos`**: 将容器内录制的视频同步保存到宿主机，删除容器后视频依然保留。
* **`./init.sql:/docker-entrypoint-initdb.d/init.sql`**: 数据库初始化脚本。

---

## 📁 项目结构

```text
match-system/
├── backend/            # Spring Boot 后端源码 (JDK 22)
│   ├── src/main/java/.../config/WebConfig.java # [关键] 静态资源映射配置
│   └── src/main/resources
├── frontend/           # Vue 3 前端源码
│   └── src/components/Scoreboard.vue  # 核心记分组件
├── videos/             # [自动生成] 存放录制的比赛视频
├── compose.yml         # Docker 一键启动配置 (包含 App + DB)
├── Dockerfile          # 后端 3 阶段构建文件 (包含 Node.js 构建前端)
├── init.sql            # 数据库初始化脚本
└── README.md           # 项目文档

```

---

