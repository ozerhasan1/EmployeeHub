package com.hasanozer.employeehub.dto.common;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String traceId,
        List<FieldErrorResponse> fieldErrors
) {
}
