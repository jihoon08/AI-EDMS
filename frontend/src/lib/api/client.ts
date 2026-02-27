import axios, { AxiosError } from 'axios';

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
    public code?: string,
    public details?: unknown
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export class NetworkError extends Error {
  constructor(message: string = '네트워크 연결에 실패했습니다') {
    super(message);
    this.name = 'NetworkError';
  }
}

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || '/api/v1',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
  paramsSerializer: {
    serialize: (params: Record<string, unknown>) => {
      const searchParams = new URLSearchParams();
      Object.entries(params).forEach(([key, value]) => {
        if (value === null || value === undefined) return;
        if (Array.isArray(value)) {
          value.forEach((v) => searchParams.append(key, String(v)));
        } else {
          searchParams.append(key, String(value));
        }
      });
      return searchParams.toString();
    },
  },
});

// 요청 인터셉터
api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const userUuid = localStorage.getItem('userUuid');
    const userEmail = localStorage.getItem('userEmail');
    const userName = localStorage.getItem('userName');

    if (userUuid) config.headers['X-User-UUID'] = userUuid;
    if (userEmail) config.headers['X-User-Email'] = userEmail;
    if (userName) config.headers['X-User-Name'] = userName;
  }

  config.headers['Accept-Language'] = 'ko';
  return config;
});

// 응답 인터셉터
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<{ error?: { code?: string; message?: string; details?: unknown } }>) => {
    if (!error.response) {
      return Promise.reject(new NetworkError());
    }

    const { status, data } = error.response;
    const serverMessage = data?.error?.message || '요청 처리 중 오류가 발생했습니다';
    const errorCode = data?.error?.code;

    if (status === 401) {
      if (typeof window !== 'undefined') {
        window.location.href = `/login?error=${encodeURIComponent(serverMessage)}`;
      }
    }

    return Promise.reject(new ApiError(serverMessage, status, errorCode, data?.error?.details));
  }
);

export default api;

// 공통 타입
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface CommonApiResponse<T> {
  success: boolean;
  data: T;
  error?: {
    code: string;
    message: string;
    details?: unknown;
  };
  timestamp: string;
  traceId: string;
}
