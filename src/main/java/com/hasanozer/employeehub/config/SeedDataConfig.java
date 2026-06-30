package com.hasanozer.employeehub.config;

import com.hasanozer.employeehub.entity.Department;
import com.hasanozer.employeehub.entity.Employee;
import com.hasanozer.employeehub.enums.EmploymentStatus;
import com.hasanozer.employeehub.repository.DepartmentRepository;
import com.hasanozer.employeehub.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class SeedDataConfig {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Bean
    CommandLineRunner seedData() {
        return args -> seed();
    }

    void seed() {
        if (departmentRepository.count() > 0 || employeeRepository.count() > 0) {
            return;
        }

        Department engineering = new Department("Engineering", "Software development and technical operations");
        Department humanResources = new Department("Human Resources", "People operations and employee support");
        Department finance = new Department("Finance", "Accounting, budgeting, and reporting");

        departmentRepository.saveAll(List.of(engineering, humanResources, finance));

        Employee alex = employee(
                "Alex",
                "Morgan",
                "alex.morgan@example.com",
                "+1-555-0101",
                "Backend Developer",
                EmploymentStatus.ACTIVE,
                LocalDate.of(2024, 2, 12),
                new BigDecimal("78000.00"),
                engineering
        );
        Employee sarah = employee(
                "Sarah",
                "Johnson",
                "sarah.johnson@example.com",
                "+1-555-0102",
                "HR Coordinator",
                EmploymentStatus.ACTIVE,
                LocalDate.of(2023, 9, 4),
                new BigDecimal("62000.00"),
                humanResources
        );
        Employee daniel = employee(
                "Daniel",
                "Lee",
                "daniel.lee@example.com",
                "+1-555-0103",
                "Accountant",
                EmploymentStatus.ON_LEAVE,
                LocalDate.of(2022, 6, 20),
                new BigDecimal("69000.00"),
                finance
        );

        employeeRepository.saveAll(List.of(alex, sarah, daniel));
    }

    private Employee employee(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String jobTitle,
            EmploymentStatus status,
            LocalDate hireDate,
            BigDecimal salary,
            Department department
    ) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPhoneNumber(phoneNumber);
        employee.setJobTitle(jobTitle);
        employee.setEmploymentStatus(status);
        employee.setHireDate(hireDate);
        employee.setSalary(salary);
        employee.setDepartment(department);
        return employee;
    }
}
