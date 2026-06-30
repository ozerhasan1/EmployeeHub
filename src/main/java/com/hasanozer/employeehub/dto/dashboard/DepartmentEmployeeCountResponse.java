package com.hasanozer.employeehub.dto.dashboard;

public record DepartmentEmployeeCountResponse(
        Long departmentId,
        String departmentName,
        long employeeCount
) {
}
