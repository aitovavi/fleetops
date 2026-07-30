package com.aitovavi.fleetops.auth.api;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {
}