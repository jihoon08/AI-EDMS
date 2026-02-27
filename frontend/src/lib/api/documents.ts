import api, { type CommonApiResponse, type PageResponse } from './client';

export interface DocumentResponse {
  documentUuid: string;
  documentNumber: string;
  title: string;
  description: string | null;
  documentType: string;
  status: string;
  securityLevel: string;
  folderUuid: string | null;
  ownerUuid: string;
  currentVersion: number;
  fileName: string;
  fileSize: number;
  contentType: string;
  retentionPeriod: string | null;
  createdAt: string;
  updatedAt: string | null;
}

export interface DocumentVersionResponse {
  versionUuid: string;
  versionNumber: number;
  fileName: string;
  fileSize: number;
  contentType: string;
  changeSummary: string | null;
  createdAt: string;
  createdByUuid: string;
}

export const documentApi = {
  list: (params?: {
    keyword?: string;
    documentType?: string;
    status?: string;
    page?: number;
    size?: number;
  }) =>
    api.get<CommonApiResponse<PageResponse<DocumentResponse>>>('/documents', { params }),

  get: (uuid: string) =>
    api.get<CommonApiResponse<DocumentResponse>>(`/documents/${uuid}`),

  upload: (metadata: {
    title: string;
    description?: string;
    documentType: string;
    securityLevel?: string;
    folderUuid?: string;
  }, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('metadata', new Blob([JSON.stringify(metadata)], { type: 'application/json' }));
    return api.post<CommonApiResponse<DocumentResponse>>('/documents', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  update: (uuid: string, data: {
    title: string;
    description?: string;
    documentType?: string;
    securityLevel?: string;
    folderUuid?: string;
  }) =>
    api.put<CommonApiResponse<DocumentResponse>>(`/documents/${uuid}`, data),

  changeStatus: (uuid: string, status: string) =>
    api.patch<CommonApiResponse<DocumentResponse>>(`/documents/${uuid}/status`, { status }),

  delete: (uuid: string) =>
    api.delete<CommonApiResponse<void>>(`/documents/${uuid}`),

  getVersions: (uuid: string) =>
    api.get<CommonApiResponse<DocumentVersionResponse[]>>(`/documents/${uuid}/versions`),

  downloadUrl: (uuid: string) =>
    `${api.defaults.baseURL}/documents/${uuid}/download`,
};
