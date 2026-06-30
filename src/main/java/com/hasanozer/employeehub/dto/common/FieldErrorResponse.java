package com.hasanozer.employeehub.dto.common;

public record FieldErrorResponse(
        String field,
        String message
) {
}
