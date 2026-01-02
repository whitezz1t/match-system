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

```bash
# 1. 克隆项目 (请替换为你的实际仓库地址)
git clone [https://github.com/YourUsername/pingpong-match-system.git](https://github.com/YourUsername/pingpong-match-system.git)
cd pingpong-match-system

# 2. 启动服务 (自动构建镜像 + 启动 MySQL)
docker compose up -d

# 3. 查看日志
docker compose logs -f match-app
