package com.hasanozer.employeehub.dto.employee;

import com.hasanozer.employeehub.enums.EmploymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeCreateRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must be at most 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must be at most 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must be at most 150 characters")
        String email,

        @Size(max = 30, message = "Phone number must be at most 30 characters")
        String phoneNumber,

        @NotBlank(message = "Job title is required")
        @Size(max = 100, message = "Job title must be at most 100 characters")
        String jobTitle,

        @NotNull(message = "Employment status is required")
        EmploymentStatus employmentStatus,

        @NotNull(message = "Hire date is required")
        @PastOrPresent(message = "Hire date cannot be in the future")
        LocalDate hireDate,

        @DecimalMin(value = "0.00", inclusive = false, message = "Salary must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Salary can have up to 10 digits and 2 decimal places")
        BigDecimal salary,

        @NotNull(message = "Department is required")
        @Positive(message = "Department id must be greater than zero")
        Long departmentId
) {
}
