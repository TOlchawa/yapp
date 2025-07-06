package com.memoritta.server.advice;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class OpenApiExceptionLogger extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println(">> Intercepted: " + request.getRequestURI());
        try {
            filterChain.doFilter(request, response);
        } catch (Throwable t) {
            if (request.getRequestURI().contains("/v3/api-docs")) {
                log.warn("Error while processing /v3/api-docs: {}", t.getMessage());
            }
            throw t;
        }
        System.out.println(">> DONE: " + request.getRequestURI());
    }
}
