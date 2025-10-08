package com.acme.vocatio.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String path = resolvePathWithinApplication(request);

        // ✅ Whitelist: Swagger / OpenAPI / Health / Auth deben pasar sin JWT
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Si no hay token, no autenticamos ni devolvemos 401 aquí
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // System.out.println("JWT Filter: No Authorization header or doesn't start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        String username;
        try {
            username = jwtService.extractClaim(jwt, Claims::getSubject);
            // System.out.println("JWT Filter: Extracted username: " + username);
        } catch (Exception ex) {
            // System.out.println("JWT Filter: Error extracting username: " + ex.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Solo setea Authentication si aún no existe
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails) && jwtService.isAccessToken(jwt)) {
                var authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String servletPath) {
        // Importante: SIN /api/v1; el servletPath ya viene sin el context-path
        return servletPath.startsWith("/v3/api-docs")
                || servletPath.startsWith("/swagger-ui")
                || servletPath.equals("/swagger-ui.html")
                || servletPath.startsWith("/swagger-resources")
                || servletPath.startsWith("/webjars")
                || servletPath.startsWith("/actuator/health")
                || servletPath.equals("/actuator")
                || servletPath.equals("/api")
                || servletPath.startsWith("/api/")
                || servletPath.startsWith("/auth/")
                || servletPath.equals("/");
    }

    private String resolvePathWithinApplication(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        String path = (requestUri == null || requestUri.isBlank()) ? "/" : requestUri;
        String ctx = (contextPath == null) ? "" : contextPath;

        if (!ctx.isEmpty() && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }

        if (path.isEmpty()) {
            return "/";
        }

        return path.startsWith("/") ? path : "/" + path;
    }
}
