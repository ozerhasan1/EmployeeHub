package com.hasanozer.employeehub.service;

import com.hasanozer.employeehub.dto.dashboard.DashboardSummaryResponse;
import com.hasanozer.employeehub.dto.dashboard.DepartmentEmployeeCountResponse;
import com.hasanozer.employeehub.dto.dashboard.RecentHireResponse;
import com.hasanozer.employeehub.entity.Employee;
import com.hasanozer.employeehub.enums.EmploymentStatus;
import com.hasanozer.employeehub.repository.DepartmentRepository;
import com.hasanozer.employeehub.repository.EmployeeRepository;
import com.hasanozer.employeehub.repository.projection.DepartmentEmployeeCountProjection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int RECENT_HIRE_LIMIT = 5;

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        return new DashboardSummaryResponse(
                employeeRepository.count(),
                employeeRepository.countByEmploymentStatus(EmploymentStatus.ACTIVE),
                employeeRepository.countByEmploymentStatus(EmploymentStatus.ON_LEAVE),
                employeeRepository.countByEmploymentStatus(EmploymentStatus.TERMINATED),
                departmentRepository.count(),
                getEmployeeCountsByDepartment(),
                getRecentHires()
        );
    }

    private List<DepartmentEmployeeCountResponse> getEmployeeCountsByDepartment() {
        return employeeRepository.countEmployeesByDepartment()
                .stream()
                .map(this::toDepartmentEmployeeCountResponse)
                .toList();
    }

    private List<RecentHireResponse> getRecentHires() {
        return employeeRepository.findRecentHires(PageRequest.of(0, RECENT_HIRE_LIMIT))
                .stream()
                .map(this::toRecentHireResponse)
                .toList();
    }

    private DepartmentEmployeeCountResponse toDepartmentEmployeeCountResponse(
            DepartmentEmployeeCountProjection projection
    ) {
        return new DepartmentEmployeeCountResponse(
                projection.getDepartmentId(),
                projection.getDepartmentName(),
                projection.getEmployeeCount()
        );
    }

    private RecentHireResponse toRecentHireResponse(Employee employee) {
        return new RecentHireResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getJobTitle(),
                employee.getDepartment().getName(),
                employee.getHireDate()
        );
    }
}
