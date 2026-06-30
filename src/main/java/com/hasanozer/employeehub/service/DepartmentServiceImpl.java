package com.hasanozer.employeehub.service;

import com.hasanozer.employeehub.dto.department.DepartmentCreateRequest;
import com.hasanozer.employeehub.dto.department.DepartmentResponse;
import com.hasanozer.employeehub.dto.department.DepartmentUpdateRequest;
import com.hasanozer.employeehub.entity.Department;
import com.hasanozer.employeehub.exception.BusinessRuleException;
import com.hasanozer.employeehub.exception.DuplicateResourceException;
import com.hasanozer.employeehub.exception.ResourceNotFoundException;
import com.hasanozer.employeehub.mapper.DepartmentMapper;
import com.hasanozer.employeehub.repository.DepartmentRepository;
import com.hasanozer.employeehub.repository.EmployeeRepository;
import com.hasanozer.employeehub.util.StringNormalizer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentCreateRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(StringNormalizer.required(request.name()))) {
            throw new DuplicateResourceException("Department name already exists");
        }

        Department department = departmentMapper.toEntity(request);
        Department saved = departmentRepository.save(department);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {
        return toResponse(findDepartment(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentUpdateRequest request) {
        Department department = findDepartment(id);
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(StringNormalizer.required(request.name()), id)) {
            throw new DuplicateResourceException("Department name already exists");
        }

        departmentMapper.updateEntity(department, request);
        return toResponse(department);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department department = findDepartment(id);
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new BusinessRuleException("Department cannot be deleted while employees are assigned to it");
        }

        departmentRepository.delete(department);
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
    }

    private DepartmentResponse toResponse(Department department) {
        return departmentMapper.toResponse(department, employeeRepository.countByDepartmentId(department.getId()));
    }
}
