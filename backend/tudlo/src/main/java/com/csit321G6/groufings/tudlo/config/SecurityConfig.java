package com.csit321G6.groufings.tudlo.config;

import com.csit321G6.groufings.tudlo.common.exception.ApiErrorResponse;
import com.csit321G6.groufings.tudlo.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUserIdNumber(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUserIdNumber())
                        .password(user.getPassword())
                        .roles(user.getUserType().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ApiErrorResponse error = new ApiErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized",
                    authException != null && authException.getMessage() != null
                            ? authException.getMessage()
                            : "Authentication required",
                    Instant.now(),
                    request.getRequestURI()
            );
            response.getWriter().write(objectMapper.writeValueAsString(error));
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ApiErrorResponse error = new ApiErrorResponse(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden",
                    accessDeniedException != null && accessDeniedException.getMessage() != null
                            ? accessDeniedException.getMessage()
                            : "Access denied",
                    Instant.now(),
                    request.getRequestURI()
            );
            response.getWriter().write(objectMapper.writeValueAsString(error));
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers("/api/users", "/api/users/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/sections", "/api/sections/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/offerings", "/api/offerings/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/my-progress/student/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/activities/student/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/scores/student/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/learning-gaps/student/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/class-lists/student/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/assessments/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                        .requestMatchers(HttpMethod.GET,
                                "/api/subjects/**",
                                "/api/class-lists/**",
                                "/api/activities/**",
                                "/api/scores/**",
                                "/api/learning-gaps/**",
                                "/api/my-progress/**"
                        ).hasAnyRole("TEACHER", "ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/sections/**",
                                "/api/offerings/**",
                                "/api/subjects/**",
                                "/api/class-lists/**",
                                "/api/assessments/**",
                                "/api/activities/**",
                                "/api/scores/**",
                                "/api/learning-gaps/**",
                                "/api/my-progress/**"
                        ).hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/sections/**",
                                "/api/offerings/**",
                                "/api/subjects/**",
                                "/api/class-lists/**",
                                "/api/assessments/**",
                                "/api/activities/**",
                                "/api/scores/**",
                                "/api/learning-gaps/**",
                                "/api/my-progress/**"
                        ).hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/sections/**",
                                "/api/offerings/**",
                                "/api/subjects/**",
                                "/api/class-lists/**",
                                "/api/assessments/**",
                                "/api/activities/**",
                                "/api/scores/**",
                                "/api/learning-gaps/**",
                                "/api/my-progress/**"
                        ).hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/sections/**",
                                "/api/offerings/**",
                                "/api/subjects/**",
                                "/api/class-lists/**",
                                "/api/assessments/**",
                                "/api/activities/**",
                                "/api/scores/**",
                                "/api/learning-gaps/**",
                                "/api/my-progress/**"
                        ).hasAnyRole("TEACHER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
