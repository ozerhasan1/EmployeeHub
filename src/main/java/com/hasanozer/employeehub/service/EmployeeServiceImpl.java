package com.hasanozer.employeehub.service;

import com.hasanozer.employeehub.dto.common.PageResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeCreateRequest;
import com.hasanozer.employeehub.dto.employee.EmployeeResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeSummaryResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeUpdateRequest;
import com.hasanozer.employeehub.entity.Department;
import com.hasanozer.employeehub.entity.Employee;
import com.hasanozer.employeehub.enums.EmploymentStatus;
import com.hasanozer.employeehub.exception.DuplicateResourceException;
import com.hasanozer.employeehub.exception.InvalidRequestException;
import com.hasanozer.employeehub.exception.ResourceNotFoundException;
import com.hasanozer.employeehub.mapper.EmployeeMapper;
import com.hasanozer.employeehub.repository.DepartmentRepository;
import com.hasanozer.employeehub.repository.EmployeeRepository;
import com.hasanozer.employeehub.util.StringNormalizer;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final String ASCENDING = "asc";
    private static final String DESCENDING = "desc";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "firstName",
            "lastName",
            "email",
            "jobTitle",
            "hireDate",
            "createdAt"
    );

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmailIgnoreCase(StringNormalizer.email(request.email()))) {
            throw new DuplicateResourceException("Employee email already exists");
        }

        Department department = findDepartment(request.departmentId());
        Employee employee = employeeMapper.toEntity(request, department);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(findEmployee(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeSummaryResponse> getAll(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String search,
            Long departmentId,
            EmploymentStatus status
    ) {
        Pageable pageable = PageRequest.of(normalizePage(page), normalizeSize(size), buildSort(sortBy, sortDirection));
        Page<Employee> employees = employeeRepository.search(normalizeSearch(search), departmentId, status, pageable);

        return new PageResponse<>(
                employees.map(employeeMapper::toSummaryResponse).getContent(),
                employees.getNumber(),
                employees.getSize(),
                employees.getNumberOfElements(),
                employees.getTotalElements(),
                employees.getTotalPages(),
                employees.isFirst(),
                employees.isLast()
        );
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        Employee employee = findEmployee(id);
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(StringNormalizer.email(request.email()), id)) {
            throw new DuplicateResourceException("Employee email already exists");
        }

        Department department = findDepartment(request.departmentId());
        employeeMapper.updateEntity(employee, request, department);
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse terminate(Long id) {
        Employee employee = findEmployee(id);
        employee.setEmploymentStatus(EmploymentStatus.TERMINATED);
        return employeeMapper.toResponse(employee);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
    }

    private int normalizePage(int page) {
        if (page < 0) {
            throw new InvalidRequestException("Page must be zero or greater");
        }
        return page;
    }

    private int normalizeSize(int size) {
        if (size < 1) {
            throw new InvalidRequestException("Page size must be greater than zero");
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private Sort buildSort(String sortBy, String sortDirection) {
        String normalizedSortBy = sortBy == null || sortBy.isBlank() ? DEFAULT_SORT_FIELD : sortBy;
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortBy)) {
            throw new InvalidRequestException("Unsupported sort field: " + normalizedSortBy);
        }

        String normalizedDirection = sortDirection == null || sortDirection.isBlank()
                ? DESCENDING
                : sortDirection.trim().toLowerCase();
        if (!ASCENDING.equals(normalizedDirection) && !DESCENDING.equals(normalizedDirection)) {
            throw new InvalidRequestException("Sort direction must be 'asc' or 'desc'");
        }

        Sort.Direction direction = ASCENDING.equals(normalizedDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, normalizedSortBy);
    }

    private String normalizeSearch(String search) {
        String normalizedSearch = StringNormalizer.nullable(search);
        return normalizedSearch == null ? "" : normalizedSearch;
    }
}
