import axios, { AxiosError } from 'axios';
import type { ApiErrorResponse } from '../types/api';

export const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

export function getApiErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<ApiErrorResponse>;
    const response = axiosError.response?.data;
    if (response?.fieldErrors?.length) {
      return response.fieldErrors.map((fieldError) => fieldError.message).join(' ');
    }
    return response?.message ?? axiosError.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Unexpected error';
}
