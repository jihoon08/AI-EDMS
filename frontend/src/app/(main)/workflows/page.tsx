'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { workflowApi, type ApprovalResponse } from '@/lib/api/workflow';
import { ClipboardCheck, Clock, CheckCircle, XCircle, ChevronDown } from 'lucide-react';
import { useState } from 'react';

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  PENDING: { label: '대기', color: 'bg-yellow-100 text-yellow-700' },
  IN_PROGRESS: { label: '진행중', color: 'bg-blue-100 text-blue-700' },
  APPROVED: { label: '승인', color: 'bg-green-100 text-green-700' },
  REJECTED: { label: '반려', color: 'bg-red-100 text-red-700' },
  CANCELLED: { label: '취소', color: 'bg-gray-100 text-gray-700' },
};

export default function WorkflowsPage() {
  const queryClient = useQueryClient();
  const [tab, setTab] = useState<'pending' | 'my-requests'>('pending');
  const [expandedId, setExpandedId] = useState<string | null>(null);
  const [comment, setComment] = useState('');

  const { data: counts } = useQuery({
    queryKey: ['workflow-counts'],
    queryFn: () => workflowApi.getCounts(),
    select: (res) => res.data.data,
  });

  const { data: pendingData, isLoading: pendingLoading } = useQuery({
    queryKey: ['workflow-pending'],
    queryFn: () => workflowApi.getPending({ size: 50 }),
    select: (res) => res.data.data,
    enabled: tab === 'pending',
  });

  const { data: myData, isLoading: myLoading } = useQuery({
    queryKey: ['workflow-my-requests'],
    queryFn: () => workflowApi.getMyRequests({ size: 50 }),
    select: (res) => res.data.data,
    enabled: tab === 'my-requests',
  });

  const approveMutation = useMutation({
    mutationFn: ({ uuid, comment }: { uuid: string; comment?: string }) =>
      workflowApi.approve(uuid, comment),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workflow-pending'] });
      queryClient.invalidateQueries({ queryKey: ['workflow-counts'] });
      setExpandedId(null);
      setComment('');
    },
  });

  const rejectMutation = useMutation({
    mutationFn: ({ uuid, comment }: { uuid: string; comment: string }) =>
      workflowApi.reject(uuid, comment),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workflow-pending'] });
      queryClient.invalidateQueries({ queryKey: ['workflow-counts'] });
      setExpandedId(null);
      setComment('');
    },
  });

  const approvals = tab === 'pending' ? pendingData?.content ?? [] : myData?.content ?? [];
  const isLoading = tab === 'pending' ? pendingLoading : myLoading;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">결재 관리</h1>

      {/* 카운트 카드 */}
      <div className="grid grid-cols-4 gap-4">
        <div className="rounded-lg border bg-white p-4">
          <div className="flex items-center gap-3">
            <Clock size={20} className="text-yellow-500" />
            <div>
              <p className="text-2xl font-bold">{counts?.pendingApprovals ?? 0}</p>
              <p className="text-xs text-gray-500">결재 대기</p>
            </div>
          </div>
        </div>
        <div className="rounded-lg border bg-white p-4">
          <div className="flex items-center gap-3">
            <ClipboardCheck size={20} className="text-blue-500" />
            <div>
              <p className="text-2xl font-bold">{counts?.myRequests ?? 0}</p>
              <p className="text-xs text-gray-500">내 요청</p>
            </div>
          </div>
        </div>
        <div className="rounded-lg border bg-white p-4">
          <div className="flex items-center gap-3">
            <CheckCircle size={20} className="text-green-500" />
            <div>
              <p className="text-2xl font-bold">{counts?.approved ?? 0}</p>
              <p className="text-xs text-gray-500">승인됨</p>
            </div>
          </div>
        </div>
        <div className="rounded-lg border bg-white p-4">
          <div className="flex items-center gap-3">
            <XCircle size={20} className="text-red-500" />
            <div>
              <p className="text-2xl font-bold">{counts?.rejected ?? 0}</p>
              <p className="text-xs text-gray-500">반려됨</p>
            </div>
          </div>
        </div>
      </div>

      {/* 탭 */}
      <div className="flex gap-1 rounded-lg bg-gray-100 p-1">
        <button
          onClick={() => setTab('pending')}
          className={`flex-1 rounded-md py-2 text-sm font-medium transition ${
            tab === 'pending' ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500'
          }`}
        >
          결재 대기 ({counts?.pendingApprovals ?? 0})
        </button>
        <button
          onClick={() => setTab('my-requests')}
          className={`flex-1 rounded-md py-2 text-sm font-medium transition ${
            tab === 'my-requests' ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500'
          }`}
        >
          내 결재 요청 ({counts?.myRequests ?? 0})
        </button>
      </div>

      {/* 결재 목록 */}
      <div className="rounded-lg border bg-white shadow-sm">
        {isLoading ? (
          <div className="p-8 text-center text-sm text-gray-500">로딩 중...</div>
        ) : approvals.length === 0 ? (
          <div className="p-8 text-center text-sm text-gray-500">
            {tab === 'pending' ? '대기 중인 결재가 없습니다.' : '결재 요청이 없습니다.'}
          </div>
        ) : (
          <div className="divide-y">
            {approvals.map((approval: ApprovalResponse) => {
              const statusInfo = STATUS_LABELS[approval.status] || { label: approval.status, color: 'bg-gray-100' };
              const isExpanded = expandedId === approval.approvalUuid;

              return (
                <div key={approval.approvalUuid}>
                  <div
                    className="flex cursor-pointer items-center gap-4 px-4 py-4 hover:bg-gray-50"
                    onClick={() => setExpandedId(isExpanded ? null : approval.approvalUuid)}
                  >
                    <div className="flex-1">
                      <p className="text-sm font-medium text-gray-900">{approval.title}</p>
                      <p className="mt-1 text-xs text-gray-500">
                        요청자: {approval.requesterName} / 단계: {approval.currentStep}/{approval.totalSteps}
                      </p>
                    </div>
                    <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${statusInfo.color}`}>
                      {statusInfo.label}
                    </span>
                    <span className="text-xs text-gray-400">
                      {new Date(approval.createdAt).toLocaleDateString('ko-KR')}
                    </span>
                    <ChevronDown size={16} className={`text-gray-400 transition ${isExpanded ? 'rotate-180' : ''}`} />
                  </div>

                  {isExpanded && (
                    <div className="border-t bg-gray-50 px-4 py-4">
                      {approval.description && (
                        <p className="mb-3 text-sm text-gray-600">{approval.description}</p>
                      )}

                      <div className="mb-4">
                        <p className="mb-2 text-xs font-medium text-gray-500">결재 단계</p>
                        <div className="flex gap-2">
                          {approval.steps.map((step) => {
                            const stepStatus = STATUS_LABELS[step.status] || { label: step.status, color: 'bg-gray-100' };
                            return (
                              <div key={step.stepUuid} className="flex-1 rounded-md border bg-white p-2">
                                <div className="flex items-center justify-between">
                                  <span className="text-xs font-medium">{step.stepName}</span>
                                  <span className={`rounded-full px-1.5 py-0.5 text-[10px] font-medium ${stepStatus.color}`}>
                                    {stepStatus.label}
                                  </span>
                                </div>
                                {step.comment && (
                                  <p className="mt-1 text-xs text-gray-500">{step.comment}</p>
                                )}
                              </div>
                            );
                          })}
                        </div>
                      </div>

                      {tab === 'pending' && (approval.status === 'PENDING' || approval.status === 'IN_PROGRESS') && (
                        <div className="space-y-2">
                          <textarea
                            value={comment}
                            onChange={(e) => setComment(e.target.value)}
                            placeholder="코멘트 (선택사항, 반려 시 필수)"
                            rows={2}
                            className="w-full rounded-md border px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
                          />
                          <div className="flex gap-2 justify-end">
                            <button
                              onClick={() => rejectMutation.mutate({ uuid: approval.approvalUuid, comment })}
                              disabled={!comment || rejectMutation.isPending}
                              className="rounded-md border border-red-300 px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-50 disabled:opacity-50"
                            >
                              반려
                            </button>
                            <button
                              onClick={() => approveMutation.mutate({ uuid: approval.approvalUuid, comment: comment || undefined })}
                              disabled={approveMutation.isPending}
                              className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
                            >
                              승인
                            </button>
                          </div>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
