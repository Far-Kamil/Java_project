package com.needed.task.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
     @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Going from root URL to index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/alerts").setViewName("forward:/index.html");
        registry.addViewController("/alerts/**").setViewName("forward:/index.html");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Только изображения и PDF — никаких .html, .js, .exe
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // Запрещаем доступ к файлам с опасными расширениями
                        if (resourcePath.contains("..") || 
                            resourcePath.toLowerCase().matches(".*\\.(html|htm|js|exe|sh|bat)$")) {
                            return null;
                        }
                        return location.createRelative(resourcePath);
                    }
                });
    }
}
