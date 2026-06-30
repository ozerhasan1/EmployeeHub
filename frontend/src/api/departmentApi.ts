import { axiosClient } from './axiosClient';
import type { Department, DepartmentFormData } from '../types/department';

export async function getDepartments(): Promise<Department[]> {
  const response = await axiosClient.get<Department[]>('/departments');
  return response.data;
}

export async function createDepartment(data: DepartmentFormData): Promise<Department> {
  const response = await axiosClient.post<Department>('/departments', data);
  return response.data;
}

export async function updateDepartment(id: number, data: DepartmentFormData): Promise<Department> {
  const response = await axiosClient.put<Department>(`/departments/${id}`, data);
  return response.data;
}

export async function deleteDepartment(id: number): Promise<void> {
  await axiosClient.delete(`/departments/${id}`);
}
