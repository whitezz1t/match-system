# 🏓 乒乓球比赛智能管理与回放系统 (Ping Pong Match System)

> 一个基于 Spring Boot + Vue 3 的现代化乒乓球比赛管理平台，支持实时记分、鹰眼视频回放和数据分析。

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![SpringBoot](https://img.shields.io/badge/Backend-Spring%20Boot-green.svg)
![Vue3](https://img.shields.io/badge/Frontend-Vue3-42b883.svg)
![Docker](https://img.shields.io/badge/Deploy-Docker-2496ed.svg)

## ✨ 项目亮点

- **⏱️ 智能记分牌**：内置乒乓球规则引擎，自动计算发球权轮换（2球/1球）、判断胜负、处理追分逻辑。
- **📹 鹰眼回放**：基于浏览器的环形缓冲录制技术，点击得分自动回溯过去 10 秒的高光时刻，无需整场录像。
- **📊 数据可视化**：生成比赛走势图、连胜统计、发球得分率分析。
- **🐳 一键部署**：全容器化设计，无需安装 Java/MySQL，一行命令即可运行。

## 📸 系统截图

*(请在这里放 2-3 张截图，例如记分牌界面、视频回放界面、选手列表)*
![Dashboard](doc/images/screenshot1.png)

## 🛠️ 技术栈

- **前端**：Vue 3, Vite, Element Plus, ECharts
- **后端**：Spring Boot 3, Spring Data JPA, MySQL 8
- **部署**：Docker, Docker Compose

## 🚀 快速开始 (Docker 方式 - 推荐)

你无需配置任何环境，只要安装了 Docker 即可运行。

1. **克隆项目**
   ```bash
   git clone [https://github.com/你的用户名/match-system.git](https://github.com/你的用户名/match-system.git)
   cd match-system