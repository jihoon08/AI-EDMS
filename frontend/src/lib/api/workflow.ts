import api, { type CommonApiResponse, type PageResponse } from './client';

export interface ApprovalStepResponse {
  stepUuid: string;
  stepOrder: number;
  stepName: string;
  stepType: string;
  approverUuid: string;
  delegateUuid: string | null;
  status: string;
  comment: string | null;
  decidedAt: string | null;
}

export interface ApprovalResponse {
  approvalUuid: string;
  documentUuid: string;
  templateUuid: string | null;
  requesterUuid: string;
  requesterName: string;
  status: string;
  title: string;
  description: string | null;
  currentStep: number;
  totalSteps: number;
  completedAt: string | null;
  cancelledAt: string | null;
  steps: ApprovalStepResponse[];
  createdAt: string;
}

export interface TemplateStepResponse {
  stepUuid: string;
  stepOrder: number;
  stepName: string;
  stepType: string;
  approverType: string;
  approverUuid: string | null;
  approverRole: string | null;
}

export interface TemplateResponse {
  templateUuid: string;
  templateName: string;
  description: string | null;
  documentType: string | null;
  activeFlag: boolean;
  steps: TemplateStepResponse[];
  createdAt: string;
}

export interface CountResponse {
  myRequests: number;
  pendingApprovals: number;
  approved: number;
  rejected: number;
}

export interface AlertResponse {
  alertUuid: string;
  alertType: string;
  title: string;
  message: string | null;
  link: string | null;
  referenceType: string | null;
  referenceUuid: string | null;
  readFlag: boolean;
  readAt: string | null;
  createdAt: string;
}

export const workflowApi = {
  // 결재 템플릿
  getTemplates: () =>
    api.get<CommonApiResponse<TemplateResponse[]>>('/workflows/templates'),

  // 결재 요청
  createApproval: (data: {
    documentUuid: string;
    templateUuid?: string;
    title: string;
    description?: string;
    approvers: { stepOrder: number; stepName: string; approverUuid: string }[];
  }) =>
    api.post<CommonApiResponse<ApprovalResponse>>('/workflows/approvals', data),

  // 결재 상세
  getApproval: (uuid: string) =>
    api.get<CommonApiResponse<ApprovalResponse>>(`/workflows/approvals/${uuid}`),

  // 승인/반려/취소
  approve: (uuid: string, comment?: string) =>
    api.post<CommonApiResponse<ApprovalResponse>>(`/workflows/approvals/${uuid}/approve`, { comment }),

  reject: (uuid: string, comment: string) =>
    api.post<CommonApiResponse<ApprovalResponse>>(`/workflows/approvals/${uuid}/reject`, { comment }),

  cancel: (uuid: string) =>
    api.post<CommonApiResponse<ApprovalResponse>>(`/workflows/approvals/${uuid}/cancel`),

  // 목록
  getMyRequests: (params?: { page?: number; size?: number }) =>
    api.get<CommonApiResponse<PageResponse<ApprovalResponse>>>('/workflows/my-requests', { params }),

  getPending: (params?: { page?: number; size?: number }) =>
    api.get<CommonApiResponse<PageResponse<ApprovalResponse>>>('/workflows/pending', { params }),

  getCounts: () =>
    api.get<CommonApiResponse<CountResponse>>('/workflows/counts'),
};

export const notificationApi = {
  getAlerts: (params?: { unreadOnly?: boolean; page?: number; size?: number }) =>
    api.get<CommonApiResponse<PageResponse<AlertResponse>>>('/notifications', { params }),

  getUnreadCount: () =>
    api.get<CommonApiResponse<{ count: number }>>('/notifications/unread-count'),

  markAsRead: (uuid: string) =>
    api.patch<CommonApiResponse<void>>(`/notifications/${uuid}/read`),

  markAllAsRead: () =>
    api.patch<CommonApiResponse<void>>('/notifications/read-all'),
};
