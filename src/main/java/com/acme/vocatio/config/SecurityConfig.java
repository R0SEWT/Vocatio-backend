package com.acme.vocatio.config;

import com.acme.vocatio.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final UserDetailsService userDetailsService;
    private final ServerProperties serverProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ Públicos (se adaptan al context-path si existe)
                        .requestMatchers(publicMatchers()).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                ))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(java.util.List.of("*"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        configuration.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private RequestMatcher[] publicMatchers() {
        List<String> rawPatterns = List.of(
                "/",
                "/auth/login",
                "/auth/register",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/actuator/health"
        );

        return rawPatterns.stream()
                .flatMap(pattern -> Stream.of(pattern, withContextPath(pattern)))
                .distinct()
                .map(this::contextAwareMatcher)
                .toArray(RequestMatcher[]::new);
    }

    private RequestMatcher contextAwareMatcher(String pattern) {
        final String normalizedPattern = normalizePattern(pattern);
        return request -> {
            String path = normalizeRequestUri(request.getRequestURI(), request.getContextPath());
            return PATH_MATCHER.match(normalizedPattern, path);
        };
    }

    private String withContextPath(String pattern) {
        String normalizedPattern = normalizePattern(pattern);
        String contextPath = serverProperties != null && serverProperties.getServlet() != null
                ? Objects.toString(serverProperties.getServlet().getContextPath(), "")
                : "";

        if (contextPath == null || contextPath.isBlank() || "/".equals(contextPath)) {
            return normalizedPattern;
        }

        if (!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }

        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }

        if ("/".equals(normalizedPattern)) {
            return contextPath.isEmpty() ? "/" : contextPath;
        }

        return contextPath + normalizedPattern;
    }

    private String normalizePattern(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return "/";
        }

        return pattern.startsWith("/") ? pattern : "/" + pattern;
    }

    private String normalizeRequestUri(String requestUri, String contextPath) {
        String normalizedUri = (requestUri == null || requestUri.isBlank()) ? "/" : requestUri;
        String ctx = (contextPath == null) ? "" : contextPath;

        if (!ctx.isEmpty() && normalizedUri.startsWith(ctx)) {
            normalizedUri = normalizedUri.substring(ctx.length());
        }

        if (normalizedUri.isEmpty()) {
            return "/";
        }

        return normalizedUri.startsWith("/") ? normalizedUri : "/" + normalizedUri;
    }
}
