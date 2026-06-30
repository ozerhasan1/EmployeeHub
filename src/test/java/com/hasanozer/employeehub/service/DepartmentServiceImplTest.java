package com.hasanozer.employeehub.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hasanozer.employeehub.dto.department.DepartmentCreateRequest;
import com.hasanozer.employeehub.dto.department.DepartmentResponse;
import com.hasanozer.employeehub.entity.Department;
import com.hasanozer.employeehub.exception.BusinessRuleException;
import com.hasanozer.employeehub.exception.DuplicateResourceException;
import com.hasanozer.employeehub.mapper.DepartmentMapper;
import com.hasanozer.employeehub.repository.DepartmentRepository;
import com.hasanozer.employeehub.repository.EmployeeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private DepartmentServiceImpl departmentService;

    @BeforeEach
    void setUp() {
        departmentService = new DepartmentServiceImpl(departmentRepository, employeeRepository, new DepartmentMapper());
    }

    @Test
    void createSavesDepartmentWithNormalizedName() {
        DepartmentCreateRequest request = new DepartmentCreateRequest("  Engineering  ", "  Product engineering  ");

        when(departmentRepository.existsByNameIgnoreCase("Engineering")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
            Department department = invocation.getArgument(0);
            department.setId(1L);
            department.setCreatedAt(LocalDateTime.of(2026, 1, 1, 8, 0));
            department.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 8, 0));
            return department;
        });
        when(employeeRepository.countByDepartmentId(1L)).thenReturn(0L);

        DepartmentResponse response = departmentService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Engineering");
        assertThat(response.description()).isEqualTo("Product engineering");
        assertThat(response.employeeCount()).isZero();
    }

    @Test
    void createRejectsDuplicateDepartmentName() {
        DepartmentCreateRequest request = new DepartmentCreateRequest("Engineering", null);

        when(departmentRepository.existsByNameIgnoreCase("Engineering")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Department name already exists");

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void getAllReturnsDepartmentsWithEmployeeCounts() {
        Department engineering = department(1L, "Engineering");
        Department operations = department(2L, "Operations");

        when(departmentRepository.findAllByOrderByNameAsc()).thenReturn(List.of(engineering, operations));
        when(employeeRepository.countByDepartmentId(1L)).thenReturn(3L);
        when(employeeRepository.countByDepartmentId(2L)).thenReturn(1L);

        List<DepartmentResponse> responses = departmentService.getAll();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).employeeCount()).isEqualTo(3L);
        assertThat(responses.get(1).employeeCount()).isEqualTo(1L);
    }

    @Test
    void deleteRejectsDepartmentThatStillHasEmployees() {
        Department department = department(1L, "Engineering");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.existsByDepartmentId(1L)).thenReturn(true);

        assertThatThrownBy(() -> departmentService.delete(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("employees are assigned");

        verify(departmentRepository, never()).delete(any());
    }

    private Department department(Long id, String name) {
        Department department = new Department(name, "Department description");
        department.setId(id);
        department.setCreatedAt(LocalDateTime.of(2026, 1, 1, 8, 0));
        department.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 8, 0));
        return department;
    }
}
