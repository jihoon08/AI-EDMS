'use client';

import { useQuery } from '@tanstack/react-query';
import { dashboardApi, type RecentDocument } from '@/lib/api/dashboard';
import { FileText, Upload, FolderOpen, GitPullRequest } from 'lucide-react';
import Link from 'next/link';

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  DRAFT: { label: '초안', color: 'bg-gray-100 text-gray-700' },
  IN_REVIEW: { label: '검토중', color: 'bg-yellow-100 text-yellow-700' },
  APPROVED: { label: '승인', color: 'bg-green-100 text-green-700' },
  REJECTED: { label: '반려', color: 'bg-red-100 text-red-700' },
  PUBLISHED: { label: '게시', color: 'bg-blue-100 text-blue-700' },
  ARCHIVED: { label: '보관', color: 'bg-purple-100 text-purple-700' },
};

const TYPE_LABELS: Record<string, string> = {
  CONTRACT: '계약서',
  REPORT: '보고서',
  MANUAL: '매뉴얼',
  REGULATION: '규정/지침',
  PROPOSAL: '기안서',
  MINUTES: '회의록',
  GENERAL: '일반 문서',
};

export default function DashboardPage() {
  const { data: overview, isLoading } = useQuery({
    queryKey: ['dashboard'],
    queryFn: () => dashboardApi.overview(),
    select: (res) => res.data.data,
    refetchInterval: 30000,
  });

  const stats = [
    {
      label: '전체 문서',
      value: overview?.totalDocuments ?? 0,
      icon: FileText,
      color: 'text-blue-600 bg-blue-50',
    },
    {
      label: '오늘 등록',
      value: overview?.todayDocuments ?? 0,
      icon: Upload,
      color: 'text-green-600 bg-green-50',
    },
    {
      label: '전체 폴더',
      value: overview?.totalFolders ?? 0,
      icon: FolderOpen,
      color: 'text-yellow-600 bg-yellow-50',
    },
    {
      label: '대기 중 결재',
      value: 0,
      icon: GitPullRequest,
      color: 'text-orange-600 bg-orange-50',
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">대시보드</h1>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => (
          <div key={stat.label} className="rounded-lg border bg-white p-6 shadow-sm">
            <div className="flex items-center gap-4">
              <div className={`rounded-lg p-3 ${stat.color}`}>
                <stat.icon size={24} />
              </div>
              <div>
                <p className="text-sm text-gray-500">{stat.label}</p>
                <p className="text-2xl font-bold text-gray-900">
                  {isLoading ? '-' : stat.value}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 최근 등록 문서 */}
        <div className="rounded-lg border bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">최근 등록 문서</h2>
          {overview?.recentDocuments && overview.recentDocuments.length > 0 ? (
            <div className="space-y-3">
              {overview.recentDocuments.map((doc: RecentDocument) => {
                const st = STATUS_LABELS[doc.status] || { label: doc.status, color: 'bg-gray-100' };
                return (
                  <Link
                    key={doc.documentUuid}
                    href={`/documents/${doc.documentUuid}`}
                    className="flex items-center justify-between rounded-md border p-3 hover:bg-gray-50"
                  >
                    <div className="flex items-center gap-3">
                      <FileText size={18} className="text-blue-500" />
                      <div>
                        <p className="text-sm font-medium">{doc.title}</p>
                        <p className="text-xs text-gray-500">
                          {doc.documentNumber} / {TYPE_LABELS[doc.documentType] || doc.documentType}
                        </p>
                      </div>
                    </div>
                    <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${st.color}`}>
                      {st.label}
                    </span>
                  </Link>
                );
              })}
            </div>
          ) : (
            <p className="text-sm text-gray-500">등록된 문서가 없습니다.</p>
          )}
        </div>

        {/* 문서 유형별 현황 */}
        <div className="rounded-lg border bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-lg font-semibold text-gray-900">문서 유형별 현황</h2>
          {overview?.byType && Object.keys(overview.byType).length > 0 ? (
            <div className="space-y-2">
              {Object.entries(overview.byType).map(([type, count]) => (
                <div key={type} className="flex items-center justify-between rounded-md bg-gray-50 px-3 py-2">
                  <span className="text-sm text-gray-700">{TYPE_LABELS[type] || type}</span>
                  <span className="text-sm font-semibold text-gray-900">{count}건</span>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-sm text-gray-500">데이터가 없습니다.</p>
          )}
        </div>
      </div>
    </div>
  );
}
