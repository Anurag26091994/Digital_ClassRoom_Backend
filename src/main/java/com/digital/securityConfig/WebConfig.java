package com.digital.securityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps /reports/** URL path to the physical reports folder
        registry.addResourceHandler("/reports/**")
                .addResourceLocations("file:C:/Users/DELL/IdeaProjects/DigitalClassRoom/reports/");
    }
}

