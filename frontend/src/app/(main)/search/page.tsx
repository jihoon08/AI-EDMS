'use client';

import { useQuery } from '@tanstack/react-query';
import { searchApi, type SearchResult } from '@/lib/api/ai';
import { Search, FileText } from 'lucide-react';
import { useState } from 'react';
import { useRouter } from 'next/navigation';

const DOC_TYPES = [
  { value: '', label: '전체' },
  { value: 'CONTRACT', label: '계약서' },
  { value: 'REPORT', label: '보고서' },
  { value: 'MANUAL', label: '매뉴얼' },
  { value: 'PLAN', label: '기획서' },
  { value: 'MINUTES', label: '회의록' },
  { value: 'OTHER', label: '기타' },
];

export default function SearchPage() {
  const router = useRouter();
  const [keyword, setKeyword] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [documentType, setDocumentType] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['search', searchKeyword, documentType],
    queryFn: () => searchApi.search({
      keyword: searchKeyword || undefined,
      documentType: documentType || undefined,
      size: 50,
    }),
    select: (res) => res.data.data,
    enabled: searchKeyword.length > 0,
  });

  const handleSearch = () => {
    if (keyword.trim()) {
      setSearchKeyword(keyword.trim());
    }
  };

  const results = data?.content ?? [];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">통합 검색</h1>

      {/* 검색바 */}
      <div className="rounded-lg border bg-white p-4 shadow-sm">
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              placeholder="제목, 내용, 태그로 검색..."
              className="w-full rounded-md border px-10 py-2.5 text-sm focus:border-blue-500 focus:outline-none"
            />
          </div>
          <select
            value={documentType}
            onChange={(e) => setDocumentType(e.target.value)}
            className="rounded-md border px-3 py-2.5 text-sm focus:border-blue-500 focus:outline-none"
          >
            {DOC_TYPES.map((t) => (
              <option key={t.value} value={t.value}>{t.label}</option>
            ))}
          </select>
          <button
            onClick={handleSearch}
            className="rounded-md bg-blue-600 px-6 py-2.5 text-sm font-medium text-white hover:bg-blue-700"
          >
            검색
          </button>
        </div>
      </div>

      {/* 결과 */}
      {searchKeyword && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <p className="text-sm text-gray-500">
              &quot;{searchKeyword}&quot; 검색 결과: {data?.totalElements ?? 0}건
            </p>
          </div>

          {isLoading ? (
            <div className="rounded-lg border bg-white p-8 text-center text-sm text-gray-500">
              검색 중...
            </div>
          ) : results.length === 0 ? (
            <div className="rounded-lg border bg-white p-8 text-center text-sm text-gray-500">
              검색 결과가 없습니다.
            </div>
          ) : (
            <div className="space-y-2">
              {results.map((doc: SearchResult) => (
                <div
                  key={doc.documentUuid}
                  onClick={() => router.push(`/documents/${doc.documentUuid}`)}
                  className="cursor-pointer rounded-lg border bg-white p-4 hover:border-blue-300 hover:shadow-sm transition"
                >
                  <div className="flex items-start gap-3">
                    <FileText size={20} className="mt-0.5 shrink-0 text-blue-500" />
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <p className="text-sm font-medium text-gray-900">{doc.title}</p>
                        <span className="rounded-full bg-gray-100 px-2 py-0.5 text-[10px] font-medium text-gray-600">
                          {doc.documentType}
                        </span>
                      </div>
                      {doc.description && (
                        <p className="mt-1 text-xs text-gray-500 line-clamp-2">{doc.description}</p>
                      )}
                      <div className="mt-2 flex items-center gap-3 text-[10px] text-gray-400">
                        <span>{doc.documentNumber}</span>
                        {doc.fileName && <span>{doc.fileName}</span>}
                        <span>{new Date(doc.createdAt).toLocaleDateString('ko-KR')}</span>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
