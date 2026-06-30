package com.hasanozer.employeehub.controller;

import com.hasanozer.employeehub.dto.department.DepartmentCreateRequest;
import com.hasanozer.employeehub.dto.department.DepartmentResponse;
import com.hasanozer.employeehub.dto.department.DepartmentUpdateRequest;
import com.hasanozer.employeehub.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Departments", description = "Department management APIs")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Create a department")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Department name already exists")
    })
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentCreateRequest request) {
        DepartmentResponse response = departmentService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/departments/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a department by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department found"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<DepartmentResponse> getById(
            @Parameter(description = "Department id", example = "1")
            @PathVariable
            @Positive
            Long id
    ) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List departments")
    @ApiResponse(responseCode = "200", description = "Departments returned")
    public ResponseEntity<List<DepartmentResponse>> getAll() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Department not found"),
            @ApiResponse(responseCode = "409", description = "Department name already exists")
    })
    public ResponseEntity<DepartmentResponse> update(
            @Parameter(description = "Department id", example = "1")
            @PathVariable
            @Positive
            Long id,
            @Valid @RequestBody DepartmentUpdateRequest request
    ) {
        return ResponseEntity.ok(departmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department deleted"),
            @ApiResponse(responseCode = "404", description = "Department not found"),
            @ApiResponse(responseCode = "409", description = "Department has assigned employees")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Department id", example = "1")
            @PathVariable
            @Positive
            Long id
    ) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
