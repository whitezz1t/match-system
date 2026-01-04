package org.example.matchsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String os = System.getProperty("os.name").toLowerCase();
        String videoPath;

        if (os.contains("win")) {
            // Windows 本地开发环境
            videoPath = System.getProperty("user.dir") + File.separator + "videos" + File.separator;
        } else {
            // Docker / Linux 环境 (直接对应 compose.yml 里的 /app/videos)
            videoPath = "/app/videos/";
        }

        // 打印一下路径，方便看日志排错
        System.out.println("🚀 视频映射路径: file:" + videoPath);

        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:" + videoPath);
    }
}