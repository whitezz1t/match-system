# ==========================================
# 阶段一：前端构建 (Node.js)
# ==========================================
FROM node:22 AS frontend-builder
WORKDIR /app_web
# 复制前端依赖配置
COPY frontend/package*.json ./
# 安装前端依赖
RUN npm install
# 复制前端源码
COPY frontend/ .
# 打包生成 dist 目录
RUN npm run build


# ==========================================
# 阶段二：后端构建 (Maven)
# ==========================================
FROM maven:3.9-eclipse-temurin-22 AS backend-builder
WORKDIR /build

# 1. 下载后端依赖
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# 2. 复制后端源码
COPY backend/src ./src

# ⭐⭐ 关键步骤：把前端打包好的 dist 放到 Spring Boot 的静态资源目录下 ⭐⭐
# Spring Boot 默认会从 src/main/resources/static 找网页
COPY --from=frontend-builder /app_web/dist ./src/main/resources/static

# 3. 打包后端 (这时候 Jar 包里已经包含了前端网页)
RUN mvn clean package -DskipTests


# ==========================================
# 阶段三：运行 (最终镜像)
# ==========================================
FROM openjdk:22-jdk-slim

# 安装字体库 (解决 Excel 导出为空的问题)
RUN apt-get update && apt-get install -y \
    fontconfig \
    libfreetype6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 从阶段二复制打包好的 Jar
COPY --from=backend-builder /build/target/*.jar app.jar

RUN mkdir videos

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]