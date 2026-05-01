package com.perfumeria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    
   
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String reportPath = uploadDir;
        
        
        if (!reportPath.endsWith("/")) {
            reportPath += "/";
        }

        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:///" + reportPath); 
                
    }

}
    

