package com.hasanozer.employeehub.service;

import com.hasanozer.employeehub.dto.common.PageResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeCreateRequest;
import com.hasanozer.employeehub.dto.employee.EmployeeResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeSummaryResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeUpdateRequest;
import com.hasanozer.employeehub.enums.EmploymentStatus;

public interface EmployeeService {

    EmployeeResponse create(EmployeeCreateRequest request);

    EmployeeResponse getById(Long id);

    PageResponse<EmployeeSummaryResponse> getAll(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String search,
            Long departmentId,
            EmploymentStatus status
    );

    EmployeeResponse update(Long id, EmployeeUpdateRequest request);

    EmployeeResponse terminate(Long id);
}
