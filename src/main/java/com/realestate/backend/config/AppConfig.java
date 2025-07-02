package com.realestate.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(Authorize -> Authorize
                        .requestMatchers("/api/otp/send","/api/otp/verify","/api/auth/registerBuyer", "/api/auth/loginBuyer", "/api/auth/registerSeller", "/api/auth/loginSeller").permitAll()
                        .requestMatchers("/api/users/{id}","/api/properties/getAll").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtValidator(), BasicAuthenticationFilter.class)
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource()) // Updated this line
                .and()
                .httpBasic()
                .and()
                .formLogin();
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // Allow specific origins (front-end URLs)
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:4200","https://estatesphere.netlify.app/"));
        // Allow all HTTP methods
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        // Allow credentials (like cookies or authorization headers)
        corsConfiguration.setAllowCredentials(true);
        // Allow all headers from the client
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        // Expose headers like Authorization
        corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));
        // Cache the CORS settings for 1 hour
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
