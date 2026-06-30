package com.hasanozer.employeehub.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentCreateRequest(
        @NotBlank(message = "Department name is required")
        @Size(max = 100, message = "Department name must be at most 100 characters")
        String name,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description
) {
}
