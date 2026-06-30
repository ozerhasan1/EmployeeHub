import { axiosClient } from './axiosClient';
import type { DashboardSummary } from '../types/dashboard';

export async function getDashboardSummary(): Promise<DashboardSummary> {
  const response = await axiosClient.get<DashboardSummary>('/dashboard/summary');
  return response.data;
}
