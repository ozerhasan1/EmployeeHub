package com.hasanozer.employeehub.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.hasanozer.employeehub.dto.dashboard.DashboardSummaryResponse;
import com.hasanozer.employeehub.entity.Department;
import com.hasanozer.employeehub.entity.Employee;
import com.hasanozer.employeehub.enums.EmploymentStatus;
import com.hasanozer.employeehub.repository.DepartmentRepository;
import com.hasanozer.employeehub.repository.EmployeeRepository;
import com.hasanozer.employeehub.repository.projection.DepartmentEmployeeCountProjection;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void getSummaryReturnsDashboardAggregates() {
        Department engineering = new Department("Engineering", "Software development");
        engineering.setId(1L);

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setFirstName("Alex");
        employee.setLastName("Morgan");
        employee.setJobTitle("Backend Developer");
        employee.setHireDate(LocalDate.of(2024, 2, 12));
        employee.setDepartment(engineering);

        when(employeeRepository.count()).thenReturn(3L);
        when(employeeRepository.countByEmploymentStatus(EmploymentStatus.ACTIVE)).thenReturn(2L);
        when(employeeRepository.countByEmploymentStatus(EmploymentStatus.ON_LEAVE)).thenReturn(1L);
        when(employeeRepository.countByEmploymentStatus(EmploymentStatus.TERMINATED)).thenReturn(0L);
        when(departmentRepository.count()).thenReturn(1L);
        when(employeeRepository.countEmployeesByDepartment()).thenReturn(List.of(departmentCount(1L, "Engineering", 3L)));
        when(employeeRepository.findRecentHires(PageRequest.of(0, 5))).thenReturn(List.of(employee));

        DashboardSummaryResponse response = dashboardService.getSummary();

        assertThat(response.totalEmployees()).isEqualTo(3);
        assertThat(response.activeEmployees()).isEqualTo(2);
        assertThat(response.onLeaveEmployees()).isEqualTo(1);
        assertThat(response.terminatedEmployees()).isZero();
        assertThat(response.totalDepartments()).isEqualTo(1);
        assertThat(response.employeesByDepartment()).hasSize(1);
        assertThat(response.employeesByDepartment().getFirst().departmentName()).isEqualTo("Engineering");
        assertThat(response.employeesByDepartment().getFirst().employeeCount()).isEqualTo(3);
        assertThat(response.recentHires()).hasSize(1);
        assertThat(response.recentHires().getFirst().firstName()).isEqualTo("Alex");
        assertThat(response.recentHires().getFirst().departmentName()).isEqualTo("Engineering");
    }

    private DepartmentEmployeeCountProjection departmentCount(Long id, String name, long count) {
        return new DepartmentEmployeeCountProjection() {
            @Override
            public Long getDepartmentId() {
                return id;
            }

            @Override
            public String getDepartmentName() {
                return name;
            }

            @Override
            public long getEmployeeCount() {
                return count;
            }
        };
    }
}
