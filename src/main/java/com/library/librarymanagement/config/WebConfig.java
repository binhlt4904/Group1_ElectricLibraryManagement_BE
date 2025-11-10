package com.library.librarymanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/books/**")
                .addResourceLocations("file:uploads/books/");

        registry.addResourceHandler("/uploads/documents/**")
                .addResourceLocations("file:uploads/documents/");

        registry.addResourceHandler("/uploads/events/**")
                .addResourceLocations("file:uploads/events/");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

