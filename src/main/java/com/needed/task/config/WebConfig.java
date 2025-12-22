package com.needed.task.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;



@Configuration
public class WebConfig implements WebMvcConfigurer {
     @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Going from root URL to index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/alerts").setViewName("forward:/index.html");
        registry.addViewController("/alerts/**").setViewName("forward:/index.html");
    }
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Только изображения и PDF — никаких .html, .js, .exe
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(@NonNull String resourcePath, 
                        @NonNull Resource location) throws IOException {
                        String path=resourcePath.toLowerCase();
                        // Запрещаем доступ к файлам с опасными расширениями
                        if (path.contains("..")) {
                            return null;
                        }
                         // Запрещаем самые опасные расширения
                        if (path.endsWith(".html") || path.endsWith(".htm") ||
                            path.endsWith(".js") || path.endsWith(".exe") ||
                            path.endsWith(".sh") || path.endsWith(".bat") ||
                            path.endsWith(".svg")) {  // SVG — потенциально опасен
                            return null;
                        }
                        return location.createRelative(resourcePath);
                    }
                });
    }
}
