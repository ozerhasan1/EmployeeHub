package com.hasanozer.employeehub.dto.employee;

import com.hasanozer.employeehub.enums.EmploymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String jobTitle,
        EmploymentStatus employmentStatus,
        LocalDate hireDate,
        BigDecimal salary,
        EmployeeDepartmentResponse department,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
