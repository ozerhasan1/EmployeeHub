package com.hasanozer.employeehub.mapper;

import com.hasanozer.employeehub.dto.department.DepartmentCreateRequest;
import com.hasanozer.employeehub.dto.department.DepartmentResponse;
import com.hasanozer.employeehub.dto.department.DepartmentUpdateRequest;
import com.hasanozer.employeehub.entity.Department;
import com.hasanozer.employeehub.util.StringNormalizer;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentCreateRequest request) {
        return new Department(StringNormalizer.required(request.name()), StringNormalizer.nullable(request.description()));
    }

    public void updateEntity(Department department, DepartmentUpdateRequest request) {
        department.setName(StringNormalizer.required(request.name()));
        department.setDescription(StringNormalizer.nullable(request.description()));
    }

    public DepartmentResponse toResponse(Department department, long employeeCount) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                employeeCount,
                department.getCreatedAt(),
                department.getUpdatedAt()
        );
    }

}
