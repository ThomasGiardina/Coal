package com.uade.tpo.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.dir}")
    private String uploadDir;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Exponer el directorio de imágenes como recurso accesible
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); // La ruta física del directorio donde se guardan las imágenes
    }
}