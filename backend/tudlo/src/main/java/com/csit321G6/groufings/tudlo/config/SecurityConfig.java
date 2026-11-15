package com.csit321G6.groufings.tudlo.config;

import java.time.Instant;
import java.util.List;
import com.csit321G6.groufings.tudlo.common.ApiError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers("/api/auth/**").authenticated()
                        .requestMatchers("/api/users", "/api/users/**",
                                "/api/admins", "/api/admins/**",
                                "/api/teachers", "/api/teachers/**",
                                "/api/system-maintenance", "/api/system-maintenance/**")
                                .hasAnyRole("DEVELOPER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/students", "/api/students/**")
                                .hasAnyRole("DEVELOPER", "ADMIN", "TEACHER")
                        .requestMatchers("/api/students", "/api/students/**")
                                .hasAnyRole("DEVELOPER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/submissions", "/api/submissions/**",
                                "/api/item-scores", "/api/item-scores/**")
                                .hasAnyRole("DEVELOPER", "TEACHER", "STUDENT")
                        .requestMatchers("/api/submissions", "/api/submissions/**",
                                "/api/item-scores", "/api/item-scores/**")
                                .hasAnyRole("DEVELOPER", "TEACHER")
                        .requestMatchers("/api/classes", "/api/classes/**",
                                "/api/enrollments", "/api/enrollments/**",
                                "/api/topics", "/api/topics/**",
                                "/api/learning-outcomes", "/api/learning-outcomes/**",
                                "/api/assessments", "/api/assessments/**",
                                "/api/assessment-items", "/api/assessment-items/**",
                                "/api/support-activities", "/api/support-activities/**")
                                .hasAnyRole("DEVELOPER", "TEACHER")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, exception) ->
                                writeSecurityError(response, HttpStatus.UNAUTHORIZED,
                                        "Authentication is required or the supplied token is invalid."))
                        .accessDeniedHandler((request, response, exception) ->
                                writeSecurityError(response, HttpStatus.FORBIDDEN,
                                        "You do not have permission to access this resource."))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService emptyUserDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origin:http://localhost:5173}") String allowedOrigin) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private void writeSecurityError(
            jakarta.servlet.http.HttpServletResponse response, HttpStatus status, String message)
            throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, null);
        response.getWriter().write("{\"timestamp\":\"" + body.timestamp()
                + "\",\"status\":" + body.status()
                + ",\"error\":\"" + body.error()
                + "\",\"message\":\"" + body.message() + "\"}");
    }
}
