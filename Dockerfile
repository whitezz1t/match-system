# ============================
# 第一阶段：在 Docker 里面构建 JAR 包
# ============================
# 使用官方 Maven 镜像 (自带 JDK)
FROM maven:3.9-eclipse-temurin-22 AS builder

# 设置工作目录
WORKDIR /build

# 1. 只是单独复制 pom.xml 并下载依赖
# (这一步是为了利用 Docker 缓存，如果源码变了但依赖没变，就不用重新下载 jar 包)
COPY backend/pom.xml .
# 注意：因为你的 pom.xml 可能在 backend 目录下，要根据实际路径调整
RUN mvn dependency:go-offline -B

# 2. 复制源代码
COPY backend/src ./src

# 3. 执行打包 (生成 jar)
RUN mvn clean package -DskipTests


# ============================
# 第二阶段：运行 (和你原来的几乎一样)
# ============================
FROM openjdk:22-jdk-slim

WORKDIR /app

# 【关键变化】
# 这里不再是从你电脑(宿主机)复制，而是从“第一阶段(builder)”那里复制生成的 jar
COPY --from=builder /build/target/*.jar app.jar

RUN mkdir videos

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]