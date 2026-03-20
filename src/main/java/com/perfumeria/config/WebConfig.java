package com.perfumeria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    // La configuración CORS se maneja en SecurityConfig
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String reportPath = uploadDir;
        
        // Si la ruta no termina en slash, se lo agregamos para que busque DENTRO
        if (!reportPath.endsWith("/")) {
            reportPath += "/";
        }

        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:///" + reportPath); 
                // 💡 Los 3 slashes son clave para rutas absolutas en Windows
    }

}
    

