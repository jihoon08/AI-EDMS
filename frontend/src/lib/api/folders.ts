import api, { type CommonApiResponse, type PageResponse } from './client';
import type { DocumentResponse } from './documents';

export interface FolderResponse {
  folderUuid: string;
  parentUuid: string | null;
  folderName: string;
  materializedPath: string;
  depth: number;
  description: string | null;
  ownerUuid: string | null;
  sortOrder: number;
}

export interface FolderTreeNode {
  folderUuid: string;
  folderName: string;
  depth: number;
  children: FolderTreeNode[];
}

export const folderApi = {
  tree: () =>
    api.get<CommonApiResponse<FolderTreeNode[]>>('/folders/tree'),

  list: (parentUuid?: string) =>
    api.get<CommonApiResponse<FolderResponse[]>>('/folders', {
      params: parentUuid ? { parentUuid } : undefined,
    }),

  get: (uuid: string) =>
    api.get<CommonApiResponse<FolderResponse>>(`/folders/${uuid}`),

  create: (data: { folderName: string; parentUuid?: string; description?: string }) =>
    api.post<CommonApiResponse<FolderResponse>>('/folders', data),

  update: (uuid: string, data: { folderName: string; description?: string }) =>
    api.put<CommonApiResponse<FolderResponse>>(`/folders/${uuid}`, data),

  delete: (uuid: string) =>
    api.delete<CommonApiResponse<void>>(`/folders/${uuid}`),

  documents: (uuid: string, params?: { page?: number; size?: number }) =>
    api.get<CommonApiResponse<PageResponse<DocumentResponse>>>(`/folders/${uuid}/documents`, { params }),
};
