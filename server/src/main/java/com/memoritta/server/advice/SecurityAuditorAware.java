package com.memoritta.server.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class SecurityAuditorAware implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                !SecurityContextHolder.getContext().getAuthentication().isAuthenticated() ||
                SecurityContextHolder.getContext().getAuthentication().getName() == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()));
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format in SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication().getName());
            return Optional.of(UUID.randomUUID());
        }
    }
}
