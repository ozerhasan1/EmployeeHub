package com.hasanozer.employeehub.service;

import com.hasanozer.employeehub.dto.department.DepartmentCreateRequest;
import com.hasanozer.employeehub.dto.department.DepartmentResponse;
import com.hasanozer.employeehub.dto.department.DepartmentUpdateRequest;
import java.util.List;

public interface DepartmentService {

    DepartmentResponse create(DepartmentCreateRequest request);

    DepartmentResponse getById(Long id);

    List<DepartmentResponse> getAll();

    DepartmentResponse update(Long id, DepartmentUpdateRequest request);

    void delete(Long id);
}
