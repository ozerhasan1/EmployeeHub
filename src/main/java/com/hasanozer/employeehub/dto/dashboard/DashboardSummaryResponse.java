package com.hasanozer.employeehub.dto.dashboard;

import java.util.List;

public record DashboardSummaryResponse(
        long totalEmployees,
        long activeEmployees,
        long onLeaveEmployees,
        long terminatedEmployees,
        long totalDepartments,
        List<DepartmentEmployeeCountResponse> employeesByDepartment,
        List<RecentHireResponse> recentHires
) {
}
