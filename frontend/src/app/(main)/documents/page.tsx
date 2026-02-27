'use client';

import { useQuery } from '@tanstack/react-query';
import { documentApi, type DocumentResponse } from '@/lib/api/documents';
import { FileText, Upload, Search } from 'lucide-react';
import Link from 'next/link';
import { useState } from 'react';

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

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('ko-KR', {
    year: 'numeric', month: '2-digit', day: '2-digit',
  });
}

export default function DocumentsPage() {
  const [keyword, setKeyword] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['documents', searchKeyword],
    queryFn: () => documentApi.list({ keyword: searchKeyword || undefined, size: 20 }),
    select: (res) => res.data.data,
  });

  const documents = data?.content ?? [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">문서 관리</h1>
        <Link
          href="/documents/upload"
          className="flex items-center gap-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          <Upload size={16} />
          문서 업로드
        </Link>
      </div>

      {/* 검색 */}
      <div className="flex gap-2">
        <div className="relative flex-1">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="문서 제목으로 검색..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && setSearchKeyword(keyword)}
            className="w-full rounded-md border px-10 py-2 text-sm focus:border-blue-500 focus:outline-none"
          />
        </div>
        <button
          onClick={() => setSearchKeyword(keyword)}
          className="rounded-md border px-4 py-2 text-sm hover:bg-gray-50"
        >
          검색
        </button>
      </div>

      {/* 문서 목록 */}
      <div className="rounded-lg border bg-white shadow-sm">
        {isLoading ? (
          <div className="p-8 text-center text-sm text-gray-500">로딩 중...</div>
        ) : documents.length === 0 ? (
          <div className="p-8 text-center">
            <FileText size={48} className="mx-auto mb-4 text-gray-300" />
            <p className="text-sm text-gray-500">등록된 문서가 없습니다.</p>
            <Link href="/documents/upload" className="mt-2 text-sm text-blue-600 hover:underline">
              첫 문서를 업로드하세요
            </Link>
          </div>
        ) : (
          <table className="w-full">
            <thead>
              <tr className="border-b bg-gray-50 text-left text-xs font-medium text-gray-500">
                <th className="px-4 py-3">문서번호</th>
                <th className="px-4 py-3">제목</th>
                <th className="px-4 py-3">유형</th>
                <th className="px-4 py-3">상태</th>
                <th className="px-4 py-3">파일</th>
                <th className="px-4 py-3">등록일</th>
              </tr>
            </thead>
            <tbody>
              {documents.map((doc: DocumentResponse) => {
                const statusInfo = STATUS_LABELS[doc.status] || { label: doc.status, color: 'bg-gray-100' };
                return (
                  <tr key={doc.documentUuid} className="border-b hover:bg-gray-50">
                    <td className="px-4 py-3 text-xs text-gray-500">{doc.documentNumber}</td>
                    <td className="px-4 py-3">
                      <Link
                        href={`/documents/${doc.documentUuid}`}
                        className="text-sm font-medium text-blue-600 hover:underline"
                      >
                        {doc.title}
                      </Link>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600">{doc.documentType}</td>
                    <td className="px-4 py-3">
                      <span className={`rounded-full px-2 py-1 text-xs font-medium ${statusInfo.color}`}>
                        {statusInfo.label}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-xs text-gray-500">
                      {doc.fileName} ({formatFileSize(doc.fileSize)})
                    </td>
                    <td className="px-4 py-3 text-xs text-gray-500">{formatDate(doc.createdAt)}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
