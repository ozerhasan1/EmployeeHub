export type EmploymentStatus = 'ACTIVE' | 'ON_LEAVE' | 'TERMINATED';

export interface EmployeeDepartment {
  id: number;
  name: string;
}

export interface EmployeeSummary {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  jobTitle: string;
  employmentStatus: EmploymentStatus;
  department: EmployeeDepartment;
}

export interface Employee extends EmployeeSummary {
  phoneNumber?: string;
  hireDate: string;
  salary?: number;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeFormData {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  jobTitle: string;
  employmentStatus: EmploymentStatus;
  hireDate: string;
  salary: string;
  departmentId: string;
}

export interface EmployeeListParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
  search?: string;
  departmentId?: string;
  status?: EmploymentStatus | '';
}
