package com.ihimp.patientservice.security;

import org.springframework.http.HttpMethod;
import tools.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   SecurityAuthenticationEntryPoint authenticationEntryPoint,
                                                   SecurityAccessDeniedHandler accessDeniedHandler) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                        "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        )
                        .permitAll()
                        .requestMatchers("/actuator/health")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/patients"
                        )
                        .hasAuthority(SecurityConstants.PATIENT_CREATE_PERMISSION)

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/patients/**"
                        )
                        .hasAuthority(SecurityConstants.PATIENT_READ_PERMISSION)

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/patients/**"
                        )
                        .hasAuthority(SecurityConstants.PATIENT_UPDATE_PERMISSION)

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/patients/**"
                        )
                        .hasAuthority(SecurityConstants.PATIENT_DELETE_PERMISSION)
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
        return http.build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }

    @Bean
    public SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint(
            ObjectMapper objectMapper) {

        return new SecurityAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public SecurityAccessDeniedHandler securityAccessDeniedHandler(
            ObjectMapper objectMapper) {

        return new SecurityAccessDeniedHandler(objectMapper);
    }
}
