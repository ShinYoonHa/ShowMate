package com.culture.CultureService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${uploadPath}") // application.properties 설정한 uploadPath
    String uploadPath;
    //uploadPath = "C:/shop
    // images/item/xxx.jpg
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/images/**")
                // /images로 시작하는 경우 uploadPath에 설정한 폴더를 기준으로 파일을
                // 읽어 오도록 설정
                .addResourceLocations(uploadPath); // 로컬 컴퓨터 에서 root 결로를 설정
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // 허용할 출처
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
