package com.hasanozer.employeehub.controller;

import com.hasanozer.employeehub.dto.common.PageResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeCreateRequest;
import com.hasanozer.employeehub.dto.employee.EmployeeResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeSummaryResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeUpdateRequest;
import com.hasanozer.employeehub.enums.EmploymentStatus;
import com.hasanozer.employeehub.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee management APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create an employee")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Department not found"),
            @ApiResponse(responseCode = "409", description = "Employee email already exists")
    })
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeResponse response = employeeService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/employees/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<EmployeeResponse> getById(
            @Parameter(description = "Employee id", example = "1")
            @PathVariable
            @Positive
            Long id
    ) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List employees with pagination, search, filters, and sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination, sorting, or filter value")
    })
    public ResponseEntity<PageResponse<EmployeeSummaryResponse>> getAll(
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be zero or greater")
            int page,

            @Parameter(description = "Page size from 1 to 100", example = "10")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be greater than zero")
            @Max(value = 100, message = "Size must be 100 or less")
            int size,

            @Parameter(description = "Sort field: firstName, lastName, email, jobTitle, hireDate, createdAt", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction: asc or desc", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection,

            @Parameter(description = "Case-insensitive search across employee and department fields", example = "john")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by department id", example = "1")
            @RequestParam(required = false)
            @Positive
            Long departmentId,

            @Parameter(description = "Filter by status")
            @RequestParam(required = false) EmploymentStatus status
    ) {
        return ResponseEntity.ok(employeeService.getAll(page, size, sortBy, sortDirection, search, departmentId, status));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Employee or department not found"),
            @ApiResponse(responseCode = "409", description = "Employee email already exists")
    })
    public ResponseEntity<EmployeeResponse> update(
            @Parameter(description = "Employee id", example = "1")
            @PathVariable
            @Positive
            Long id,
            @Valid @RequestBody EmployeeUpdateRequest request
    ) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @PatchMapping("/{id}/terminate")
    @Operation(summary = "Terminate an employee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee terminated"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<EmployeeResponse> terminate(
            @Parameter(description = "Employee id", example = "1")
            @PathVariable
            @Positive
            Long id
    ) {
        return ResponseEntity.ok(employeeService.terminate(id));
    }
}
