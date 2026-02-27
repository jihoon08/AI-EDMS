import api, { type CommonApiResponse } from './client';

export interface UserInfo {
  userUuid: string;
  email: string;
  name: string;
  department: string | null;
  position: string | null;
  profileImage: string | null;
  roles: string[];
  permissions: string[];
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: UserInfo;
}

export interface UserResponse {
  userUuid: string;
  email: string;
  name: string;
  department: string | null;
  position: string | null;
  phone: string | null;
  provider: string;
  activeFlag: boolean;
  lastLoginAt: string | null;
  createdAt: string;
  roles: string[];
}

export interface RoleResponse {
  roleUuid: string;
  roleCode: string;
  roleName: string;
  description: string | null;
}

export const authApi = {
  login: (email: string) =>
    api.post<CommonApiResponse<LoginResponse>>('/auth/login', { email }),

  me: () =>
    api.get<CommonApiResponse<UserInfo>>('/auth/me'),

  roles: () =>
    api.get<CommonApiResponse<RoleResponse[]>>('/auth/roles'),
};

export const userApi = {
  list: (params?: { keyword?: string; page?: number; size?: number }) =>
    api.get<CommonApiResponse<import('./client').PageResponse<UserResponse>>>('/users', { params }),

  get: (uuid: string) =>
    api.get<CommonApiResponse<UserResponse>>(`/users/${uuid}`),

  update: (uuid: string, data: { name?: string; department?: string; position?: string; phone?: string }) =>
    api.put<CommonApiResponse<UserResponse>>(`/users/${uuid}`, data),

  toggleActive: (uuid: string) =>
    api.patch<CommonApiResponse<void>>(`/users/${uuid}/toggle-active`),

  assignRole: (uuid: string, roleCode: string) =>
    api.post<CommonApiResponse<void>>(`/users/${uuid}/roles/${roleCode}`),
};
