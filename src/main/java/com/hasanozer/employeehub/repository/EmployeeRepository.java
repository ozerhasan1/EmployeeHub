package com.hasanozer.employeehub.repository;

import com.hasanozer.employeehub.entity.Employee;
import com.hasanozer.employeehub.enums.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByDepartmentId(Long departmentId);

    long countByDepartmentId(Long departmentId);

    @EntityGraph(attributePaths = "department")
    @Query("""
            select e from Employee e
            join e.department d
            where (:search is null or
                   lower(e.firstName) like lower(concat('%', :search, '%')) or
                   lower(e.lastName) like lower(concat('%', :search, '%')) or
                   lower(e.email) like lower(concat('%', :search, '%')) or
                   lower(e.jobTitle) like lower(concat('%', :search, '%')) or
                   lower(d.name) like lower(concat('%', :search, '%')))
              and (:departmentId is null or d.id = :departmentId)
              and (:status is null or e.employmentStatus = :status)
            """)
    Page<Employee> search(
            @Param("search") String search,
            @Param("departmentId") Long departmentId,
            @Param("status") EmploymentStatus status,
            Pageable pageable
    );
}
