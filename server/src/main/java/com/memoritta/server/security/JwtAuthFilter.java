package com.memoritta.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
            filterChain.doFilter(request, response);
            return;
        // If Authorization header is missing or not a Bearer token,
        // let other security filters (e.g. Basic auth) handle it
        filterChain.doFilter(request, response);

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final List<String> publicEndpoints = List.of(
            "/user",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
    );

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                jwtUtil.extractUsername(token);
                filterChain.doFilter(request, response);
                return;
            } catch (JwtException | IllegalArgumentException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean isPublic(String path) {
        return publicEndpoints.stream().anyMatch(path::startsWith);
    }
}
