# === 第一阶段：构建 (Build Stage) ===
FROM node:22 as build-stage

# 设置工作目录
WORKDIR /app

# 先复制 package.json 安装依赖 (利用缓存优化速度)
COPY package*.json ./
RUN npm install

# 复制源代码并打包
COPY . .
RUN npm run build

# === 第二阶段：部署 (Production Stage) ===
FROM nginx:stable-alpine as production-stage

# 复制第一阶段构建好的 dist 文件夹到 Nginx 目录
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 复制我们在上一步写的 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]