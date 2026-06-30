package com.hasanozer.employeehub.repository.projection;

public interface DepartmentEmployeeCountProjection {

    Long getDepartmentId();

    String getDepartmentName();

    long getEmployeeCount();
}
