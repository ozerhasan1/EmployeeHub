package com.hasanozer.employeehub.dto.department;

import java.time.LocalDateTime;

public record DepartmentResponse(
        Long id,
        String name,
        String description,
        long employeeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
