export interface Department {
  id: number;
  name: string;
  description?: string;
  employeeCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface DepartmentFormData {
  name: string;
  description: string;
}
