package com.fund.server.accountservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter  corsWebFilter(){

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");

        // methods (needed for preflight)
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"));
        config.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin"
                )
        );
        config.setExposedHeaders(
                List.of("Authorization")
        );
        config.setAllowCredentials(true);
        // headers Angular might send
        config.setAllowedHeaders(List.of("*"));
        //url base
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);

        return new CorsWebFilter(source);
    }
}
