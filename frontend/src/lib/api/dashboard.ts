import api, { type CommonApiResponse } from './client';

export interface DashboardOverview {
  totalDocuments: number;
  todayDocuments: number;
  totalFolders: number;
  byStatus: Record<string, number>;
  byType: Record<string, number>;
  recentDocuments: RecentDocument[];
}

export interface RecentDocument {
  documentUuid: string;
  documentNumber: string;
  title: string;
  documentType: string;
  status: string;
  fileName: string;
  createdAt: string;
}

export const dashboardApi = {
  overview: () =>
    api.get<CommonApiResponse<DashboardOverview>>('/dashboard'),
};
