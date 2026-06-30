package com.hasanozer.employeehub.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hasanozer.employeehub.dto.common.PageResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeCreateRequest;
import com.hasanozer.employeehub.dto.employee.EmployeeResponse;
import com.hasanozer.employeehub.entity.Department;
import com.hasanozer.employeehub.entity.Employee;
import com.hasanozer.employeehub.enums.EmploymentStatus;
import com.hasanozer.employeehub.exception.DuplicateResourceException;
import com.hasanozer.employeehub.exception.InvalidRequestException;
import com.hasanozer.employeehub.mapper.EmployeeMapper;
import com.hasanozer.employeehub.repository.DepartmentRepository;
import com.hasanozer.employeehub.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, departmentRepository, new EmployeeMapper());
    }

    @Test
    void createSavesEmployeeWithNormalizedEmail() {
        Department department = department(10L, "Engineering");
        EmployeeCreateRequest request = createRequest("  Alex  ", "  Rivera  ", "  ALEX.RIVERA@EXAMPLE.COM  ", 10L);

        when(employeeRepository.existsByEmailIgnoreCase("alex.rivera@example.com")).thenReturn(false);
        when(departmentRepository.findById(10L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId(1L);
            employee.setCreatedAt(LocalDateTime.of(2026, 1, 1, 9, 0));
            employee.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 9, 0));
            return employee;
        });

        EmployeeResponse response = employeeService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.firstName()).isEqualTo("Alex");
        assertThat(response.lastName()).isEqualTo("Rivera");
        assertThat(response.email()).isEqualTo("alex.rivera@example.com");
        assertThat(response.department().id()).isEqualTo(10L);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());
        assertThat(employeeCaptor.getValue().getEmail()).isEqualTo("alex.rivera@example.com");
    }

    @Test
    void createRejectsDuplicateEmail() {
        EmployeeCreateRequest request = createRequest("Alex", "Rivera", "alex.rivera@example.com", 10L);

        when(employeeRepository.existsByEmailIgnoreCase("alex.rivera@example.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Employee email already exists");

        verify(departmentRepository, never()).findById(any());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void getAllNormalizesBlankSearchAndReturnsPagedResponse() {
        Department department = department(10L, "Engineering");
        Employee employee = employee(1L, "Alex", "Rivera", "alex.rivera@example.com", EmploymentStatus.ACTIVE, department);

        when(employeeRepository.search(eq(""), eq(10L), eq(EmploymentStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employee), PageRequest.of(0, 20), 1));

        PageResponse<?> response = employeeService.getAll(
                0,
                20,
                "lastName",
                "asc",
                "   ",
                10L,
                EmploymentStatus.ACTIVE
        );

        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.content()).hasSize(1);
    }

    @Test
    void getAllRejectsUnsafeSortField() {
        assertThatThrownBy(() -> employeeService.getAll(0, 20, "salary", "asc", null, null, null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Unsupported sort field");

        verify(employeeRepository, never()).search(any(), any(), any(), any());
    }

    @Test
    void terminateMarksEmployeeTerminated() {
        Department department = department(10L, "Engineering");
        Employee employee = employee(1L, "Alex", "Rivera", "alex.rivera@example.com", EmploymentStatus.ACTIVE, department);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.terminate(1L);

        assertThat(response.employmentStatus()).isEqualTo(EmploymentStatus.TERMINATED);
        assertThat(employee.getEmploymentStatus()).isEqualTo(EmploymentStatus.TERMINATED);
    }

    private EmployeeCreateRequest createRequest(String firstName, String lastName, String email, Long departmentId) {
        return new EmployeeCreateRequest(
                firstName,
                lastName,
                email,
                "555-0100",
                "Software Engineer",
                EmploymentStatus.ACTIVE,
                LocalDate.of(2025, 1, 15),
                new BigDecimal("85000.00"),
                departmentId
        );
    }

    private Department department(Long id, String name) {
        Department department = new Department(name, "Department description");
        department.setId(id);
        department.setCreatedAt(LocalDateTime.of(2026, 1, 1, 8, 0));
        department.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 8, 0));
        return department;
    }

    private Employee employee(
            Long id,
            String firstName,
            String lastName,
            String email,
            EmploymentStatus status,
            Department department
    ) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPhoneNumber("555-0100");
        employee.setJobTitle("Software Engineer");
        employee.setEmploymentStatus(status);
        employee.setHireDate(LocalDate.of(2025, 1, 15));
        employee.setSalary(new BigDecimal("85000.00"));
        employee.setDepartment(department);
        employee.setCreatedAt(LocalDateTime.of(2026, 1, 1, 9, 0));
        employee.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 9, 0));
        return employee;
    }
}
