package com.hasanozer.employeehub.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hasanozer.employeehub.dto.dashboard.DashboardSummaryResponse;
import com.hasanozer.employeehub.dto.dashboard.DepartmentEmployeeCountResponse;
import com.hasanozer.employeehub.dto.dashboard.RecentHireResponse;
import com.hasanozer.employeehub.service.DashboardService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void getSummaryReturnsDashboardSummary() throws Exception {
        DashboardSummaryResponse summary = new DashboardSummaryResponse(
                3,
                2,
                1,
                0,
                2,
                List.of(new DepartmentEmployeeCountResponse(1L, "Engineering", 2)),
                List.of(new RecentHireResponse(10L, "Alex", "Rivera", "Software Engineer", "Engineering",
                        LocalDate.of(2026, 1, 15)))
        );

        when(dashboardService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/v1/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(3))
                .andExpect(jsonPath("$.activeEmployees").value(2))
                .andExpect(jsonPath("$.onLeaveEmployees").value(1))
                .andExpect(jsonPath("$.terminatedEmployees").value(0))
                .andExpect(jsonPath("$.totalDepartments").value(2))
                .andExpect(jsonPath("$.employeesByDepartment[0].departmentName").value("Engineering"))
                .andExpect(jsonPath("$.recentHires[0].firstName").value("Alex"));
    }
}
