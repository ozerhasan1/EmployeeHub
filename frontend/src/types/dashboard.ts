export interface DepartmentEmployeeCount {
  departmentId: number;
  departmentName: string;
  employeeCount: number;
}

export interface RecentHire {
  id: number;
  firstName: string;
  lastName: string;
  jobTitle: string;
  departmentName: string;
  hireDate: string;
}

export interface DashboardSummary {
  totalEmployees: number;
  activeEmployees: number;
  onLeaveEmployees: number;
  terminatedEmployees: number;
  totalDepartments: number;
  employeesByDepartment: DepartmentEmployeeCount[];
  recentHires: RecentHire[];
}
