package com.acme.vocatio.config;

import com.acme.vocatio.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF off para API stateless
                .csrf(csrf -> csrf.disable())
                // CORS habilitado (ver bean abajo)
                .cors(cors -> {})
                // Stateless: usamos JWT
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Rutas públicas dentro del context-path /api/v1
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicPaths())
                        .permitAll()
                        .anyRequest().authenticated()
                )
                // 401 cuando no hay autenticación válida
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                )
                // Provider con UserDetailsService + BCrypt
                .authenticationProvider(authenticationProvider())
                // Filtro JWT antes del UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private String[] publicPaths() {
        String normalizedContextPath = normalizeContextPath(contextPath);

        var patterns = new java.util.LinkedHashSet<String>();

        if (!normalizedContextPath.isEmpty()) {
            patterns.add(normalizedContextPath);
        }

        patterns.add(joinPath(normalizedContextPath, "/"));
        patterns.add(joinPath(normalizedContextPath, "/auth/register"));
        patterns.add(joinPath(normalizedContextPath, "/auth/login"));
        patterns.add(joinPath(normalizedContextPath, "/v3/api-docs/**"));
        patterns.add(joinPath(normalizedContextPath, "/swagger-ui/**"));
        patterns.add(joinPath(normalizedContextPath, "/swagger-ui.html"));
        patterns.add(joinPath(normalizedContextPath, "/swagger-resources/**"));
        patterns.add(joinPath(normalizedContextPath, "/webjars/**"));
        patterns.add(joinPath(normalizedContextPath, "/actuator/health"));

        return patterns.toArray(String[]::new);
    }

    private String normalizeContextPath(String rawContextPath) {
        if (rawContextPath == null || rawContextPath.isBlank()) {
            return "";
        }

        if ("/".equals(rawContextPath)) {
            return "";
        }

        if (rawContextPath.endsWith("/")) {
            return rawContextPath.substring(0, rawContextPath.length() - 1);
        }

        return rawContextPath;
    }

    private String joinPath(String normalizedContextPath, String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return normalizedContextPath;
        }

        if (normalizedContextPath.isEmpty()) {
            return pattern;
        }

        if ("/".equals(pattern)) {
            return normalizedContextPath + "/";
        }

        if (pattern.startsWith("/")) {
            return normalizedContextPath + pattern;
        }

        return normalizedContextPath + "/" + pattern;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS permisivo (ajustar en dominios de prod)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        cfg.addAllowedOriginPattern("*");
        cfg.addAllowedHeader("*");
        cfg.addAllowedMethod("*");
        cfg.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}