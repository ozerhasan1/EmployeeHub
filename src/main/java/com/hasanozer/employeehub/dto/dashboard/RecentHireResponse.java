package com.hasanozer.employeehub.dto.dashboard;

import java.time.LocalDate;

public record RecentHireResponse(
        Long id,
        String firstName,
        String lastName,
        String jobTitle,
        String departmentName,
        LocalDate hireDate
) {
}
