# 1. 基础镜像：使用 OpenJDK 17 (根据你的 SpringBoot 版本选择，通常是 17)
FROM openjdk:22-jdk-slim

# 2. 设置容器内的工作目录
WORKDIR /app

# 3. 将本地构建好的 JAR 包复制到容器内的 /app 目录下，并重命名为 app.jar
# 注意：这里的路径要根据你 Dockerfile 所在的位置相对于 jar 包的位置来写
COPY backend/target/match-system-0.0.1-SNAPSHOT.jar app.jar

# 4. 创建一个用于存放视频的目录 (为了防止权限问题)
RUN mkdir videos

# 5. 暴露端口 (告诉 Docker 这个容器会用 8080)
EXPOSE 8080

# 6. 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]