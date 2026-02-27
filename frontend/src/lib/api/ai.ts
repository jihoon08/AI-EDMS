import api, { type CommonApiResponse, type PageResponse } from './client';

export interface QaResponse {
  qaUuid: string;
  question: string;
  answer: string;
  sourceDocuments: { title: string; documentUuid: string }[] | null;
  modelVersion: string | null;
  feedback: string | null;
  createdAt: string;
}

export interface SearchResult {
  documentUuid: string;
  documentNumber: string;
  title: string;
  description: string | null;
  documentType: string;
  status: string;
  securityLevel: string | null;
  fileName: string | null;
  createdAt: string;
  relevanceScore: number;
}

export interface AiStatusResponse {
  pending: number;
  processing: number;
  completed: number;
  failed: number;
}

export const aiApi = {
  askQuestion: (question: string) =>
    api.post<CommonApiResponse<QaResponse>>('/ai/qa', { question }),

  getQaHistory: (params?: { page?: number; size?: number }) =>
    api.get<CommonApiResponse<PageResponse<QaResponse>>>('/ai/qa/history', { params }),

  setFeedback: (qaUuid: string, feedback: string) =>
    api.patch<CommonApiResponse<void>>(`/ai/qa/${qaUuid}/feedback`, { feedback }),

  getAiStatus: () =>
    api.get<CommonApiResponse<AiStatusResponse>>('/ai/status'),
};

export const searchApi = {
  search: (params: {
    keyword?: string;
    documentType?: string;
    status?: string;
    page?: number;
    size?: number;
  }) =>
    api.get<CommonApiResponse<PageResponse<SearchResult>>>('/search', { params }),
};
