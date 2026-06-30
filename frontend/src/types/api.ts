export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  numberOfElements: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  traceId: string;
  fieldErrors?: Array<{
    field: string;
    message: string;
  }>;
}
