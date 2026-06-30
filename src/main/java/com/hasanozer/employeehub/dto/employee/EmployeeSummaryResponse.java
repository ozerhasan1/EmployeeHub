package com.hasanozer.employeehub.dto.employee;

import com.hasanozer.employeehub.enums.EmploymentStatus;

public record EmployeeSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String jobTitle,
        EmploymentStatus employmentStatus,
        EmployeeDepartmentResponse department
) {
}
