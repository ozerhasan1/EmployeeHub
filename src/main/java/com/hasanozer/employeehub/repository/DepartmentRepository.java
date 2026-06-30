package com.hasanozer.employeehub.repository;

import com.hasanozer.employeehub.entity.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Department> findAllByOrderByNameAsc();
}
