package com.josue.pedidos_ms.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile({ "dev", "test" }) // Solo en desarrollo y testing
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(@NonNull CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("*") // ⚠️ SOLO PARA DESARROLLO - PERMITE TODO
        .allowedMethods("*") // ⚠️ SOLO PARA DESARROLLO - PERMITE TODOS LOS MÉTODOS
        .allowedHeaders("*") // ⚠️ SOLO PARA DESARROLLO - PERMITE TODOS LOS HEADERS
        .allowCredentials(false) // Debe ser false cuando allowedOrigins es "*"
        .maxAge(3600);
  }
}
