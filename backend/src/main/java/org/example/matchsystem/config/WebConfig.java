package org.example.matchsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取当前项目根目录下的 videos 文件夹路径
        String videoPath = System.getProperty("user.dir") + File.separator + "videos" + File.separator;

        // 确保目录存在
        new File(videoPath).mkdirs();

        // 映射 URL: /videos/** -> 本地 videos 目录
        // 比如访问 http://localhost:8080/videos/1.webm 就会去读取本地的 videos/1.webm
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:" + videoPath);
    }
}