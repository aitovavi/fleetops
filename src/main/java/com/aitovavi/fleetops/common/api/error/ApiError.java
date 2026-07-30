package com.aitovavi.fleetops.common.api.error;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {
}