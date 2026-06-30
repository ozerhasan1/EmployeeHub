import { axiosClient } from './axiosClient';
import type { PageResponse } from '../types/api';
import type { Employee, EmployeeFormData, EmployeeListParams, EmployeeSummary } from '../types/employee';

function toEmployeeRequest(data: EmployeeFormData) {
  return {
    firstName: data.firstName,
    lastName: data.lastName,
    email: data.email,
    phoneNumber: data.phoneNumber || null,
    jobTitle: data.jobTitle,
    employmentStatus: data.employmentStatus,
    hireDate: data.hireDate,
    salary: data.salary ? Number(data.salary) : null,
    departmentId: Number(data.departmentId),
  };
}

export async function getEmployees(params: EmployeeListParams): Promise<PageResponse<EmployeeSummary>> {
  const response = await axiosClient.get<PageResponse<EmployeeSummary>>('/employees', { params });
  return response.data;
}

export async function getEmployee(id: number): Promise<Employee> {
  const response = await axiosClient.get<Employee>(`/employees/${id}`);
  return response.data;
}

export async function createEmployee(data: EmployeeFormData): Promise<Employee> {
  const response = await axiosClient.post<Employee>('/employees', toEmployeeRequest(data));
  return response.data;
}

export async function updateEmployee(id: number, data: EmployeeFormData): Promise<Employee> {
  const response = await axiosClient.put<Employee>(`/employees/${id}`, toEmployeeRequest(data));
  return response.data;
}

export async function terminateEmployee(id: number): Promise<Employee> {
  const response = await axiosClient.patch<Employee>(`/employees/${id}/terminate`);
  return response.data;
}
