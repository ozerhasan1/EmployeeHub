package com.hasanozer.employeehub.mapper;

import com.hasanozer.employeehub.dto.employee.EmployeeCreateRequest;
import com.hasanozer.employeehub.dto.employee.EmployeeDepartmentResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeSummaryResponse;
import com.hasanozer.employeehub.dto.employee.EmployeeUpdateRequest;
import com.hasanozer.employeehub.entity.Department;
import com.hasanozer.employeehub.entity.Employee;
import com.hasanozer.employeehub.enums.EmploymentStatus;
import com.hasanozer.employeehub.util.StringNormalizer;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeCreateRequest request, Department department) {
        Employee employee = new Employee();
        apply(employee, request.firstName(), request.lastName(), request.email(), request.phoneNumber(),
                request.jobTitle(), request.employmentStatus(), request.hireDate(), request.salary(), department);
        return employee;
    }

    public void updateEntity(Employee employee, EmployeeUpdateRequest request, Department department) {
        apply(employee, request.firstName(), request.lastName(), request.email(), request.phoneNumber(),
                request.jobTitle(), request.employmentStatus(), request.hireDate(), request.salary(), department);
    }

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getJobTitle(),
                employee.getEmploymentStatus(),
                employee.getHireDate(),
                employee.getSalary(),
                toDepartmentResponse(employee.getDepartment()),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }

    public EmployeeSummaryResponse toSummaryResponse(Employee employee) {
        return new EmployeeSummaryResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getJobTitle(),
                employee.getEmploymentStatus(),
                toDepartmentResponse(employee.getDepartment())
        );
    }

    private EmployeeDepartmentResponse toDepartmentResponse(Department department) {
        return new EmployeeDepartmentResponse(department.getId(), department.getName());
    }

    private void apply(
            Employee employee,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String jobTitle,
            EmploymentStatus employmentStatus,
            LocalDate hireDate,
            BigDecimal salary,
            Department department
    ) {
        employee.setFirstName(StringNormalizer.required(firstName));
        employee.setLastName(StringNormalizer.required(lastName));
        employee.setEmail(StringNormalizer.email(email));
        employee.setPhoneNumber(StringNormalizer.nullable(phoneNumber));
        employee.setJobTitle(StringNormalizer.required(jobTitle));
        employee.setEmploymentStatus(employmentStatus);
        employee.setHireDate(hireDate);
        employee.setSalary(salary);
        employee.setDepartment(department);
    }
}
