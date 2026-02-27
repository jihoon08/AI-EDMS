'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { documentApi } from '@/lib/api/documents';
import { useParams, useRouter } from 'next/navigation';
import { ArrowLeft, Download, Trash2, Clock } from 'lucide-react';

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  DRAFT: { label: '초안', color: 'bg-gray-100 text-gray-700' },
  IN_REVIEW: { label: '검토중', color: 'bg-yellow-100 text-yellow-700' },
  APPROVED: { label: '승인', color: 'bg-green-100 text-green-700' },
  REJECTED: { label: '반려', color: 'bg-red-100 text-red-700' },
  PUBLISHED: { label: '게시', color: 'bg-blue-100 text-blue-700' },
  ARCHIVED: { label: '보관', color: 'bg-purple-100 text-purple-700' },
};

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
}

export default function DocumentDetailPage() {
  const params = useParams();
  const router = useRouter();
  const queryClient = useQueryClient();
  const documentUuid = params.id as string;

  const { data: doc, isLoading } = useQuery({
    queryKey: ['document', documentUuid],
    queryFn: () => documentApi.get(documentUuid),
    select: (res) => res.data.data,
  });

  const { data: versions } = useQuery({
    queryKey: ['document-versions', documentUuid],
    queryFn: () => documentApi.getVersions(documentUuid),
    select: (res) => res.data.data,
  });

  const deleteMutation = useMutation({
    mutationFn: () => documentApi.delete(documentUuid),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['documents'] });
      router.push('/documents');
    },
  });

  if (isLoading) {
    return <div className="p-8 text-center text-sm text-gray-500">로딩 중...</div>;
  }

  if (!doc) {
    return <div className="p-8 text-center text-sm text-red-500">문서를 찾을 수 없습니다.</div>;
  }

  const statusInfo = STATUS_LABELS[doc.status] || { label: doc.status, color: 'bg-gray-100' };

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center gap-4">
        <button onClick={() => router.back()} className="rounded-md p-2 hover:bg-gray-100">
          <ArrowLeft size={20} />
        </button>
        <div className="flex-1">
          <h1 className="text-2xl font-bold text-gray-900">{doc.title}</h1>
          <p className="text-sm text-gray-500">{doc.documentNumber}</p>
        </div>
        <div className="flex gap-2">
          <a
            href={documentApi.downloadUrl(documentUuid)}
            className="flex items-center gap-2 rounded-md border px-3 py-2 text-sm hover:bg-gray-50"
          >
            <Download size={16} /> 다운로드
          </a>
          <button
            onClick={() => {
              if (confirm('문서를 삭제하시겠습니까?')) deleteMutation.mutate();
            }}
            className="flex items-center gap-2 rounded-md border border-red-200 px-3 py-2 text-sm text-red-600 hover:bg-red-50"
          >
            <Trash2 size={16} /> 삭제
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        {/* 문서 정보 */}
        <div className="lg:col-span-2 space-y-6">
          <div className="rounded-lg border bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-lg font-semibold">문서 정보</h2>
            {doc.description && (
              <p className="mb-4 text-sm text-gray-600">{doc.description}</p>
            )}
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span className="text-gray-500">문서 유형</span>
                <p className="font-medium">{doc.documentType}</p>
              </div>
              <div>
                <span className="text-gray-500">상태</span>
                <p>
                  <span className={`rounded-full px-2 py-1 text-xs font-medium ${statusInfo.color}`}>
                    {statusInfo.label}
                  </span>
                </p>
              </div>
              <div>
                <span className="text-gray-500">보안 등급</span>
                <p className="font-medium">{doc.securityLevel}</p>
              </div>
              <div>
                <span className="text-gray-500">현재 버전</span>
                <p className="font-medium">v{doc.currentVersion}</p>
              </div>
              <div>
                <span className="text-gray-500">파일</span>
                <p className="font-medium">{doc.fileName} ({formatFileSize(doc.fileSize)})</p>
              </div>
              <div>
                <span className="text-gray-500">등록일</span>
                <p className="font-medium">{new Date(doc.createdAt).toLocaleString('ko-KR')}</p>
              </div>
            </div>
          </div>
        </div>

        {/* 버전 이력 */}
        <div className="rounded-lg border bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-lg font-semibold">버전 이력</h2>
          {versions && versions.length > 0 ? (
            <div className="space-y-3">
              {versions.map((v) => (
                <div key={v.versionUuid} className="flex items-start gap-3 text-sm">
                  <Clock size={14} className="mt-1 text-gray-400" />
                  <div>
                    <p className="font-medium">v{v.versionNumber}</p>
                    <p className="text-xs text-gray-500">
                      {v.changeSummary || v.fileName}
                    </p>
                    <p className="text-xs text-gray-400">
                      {new Date(v.createdAt).toLocaleString('ko-KR')}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-sm text-gray-500">버전 이력이 없습니다.</p>
          )}
        </div>
      </div>
    </div>
  );
}
