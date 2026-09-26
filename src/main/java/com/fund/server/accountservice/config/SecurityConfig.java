package com.fund.server.accountservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        // public API
                        .pathMatchers(
                                "/api/v1/auth/login"
                        )
                        .permitAll()


                        // protected API
                        /*
                        .pathMatchers(
                                "/api/v1/customer-profile/**"
                        )
                        .authenticated()
                        */

                        .anyExchange()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                )

                .build();
    }

    @Bean
    Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter(){

        JwtGrantedAuthoritiesConverter converter =
                new JwtGrantedAuthoritiesConverter();


        converter.setAuthoritiesClaimName(
                "realm_access.roles"
        );

        converter.setAuthorityPrefix(
                "ROLE_"
        );

        JwtAuthenticationConverter jwtConverter =
                new JwtAuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(
                converter
        );

        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }
}