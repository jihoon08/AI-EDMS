'use client';

import { useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { documentApi } from '@/lib/api/documents';
import { Upload, X, FileText } from 'lucide-react';

const DOCUMENT_TYPES = [
  { value: 'CONTRACT', label: '계약서' },
  { value: 'REPORT', label: '보고서' },
  { value: 'MANUAL', label: '매뉴얼' },
  { value: 'REGULATION', label: '규정/지침' },
  { value: 'PROPOSAL', label: '기안서' },
  { value: 'MINUTES', label: '회의록' },
  { value: 'GENERAL', label: '일반 문서' },
];

const SECURITY_LEVELS = [
  { value: 'PUBLIC', label: '공개' },
  { value: 'INTERNAL', label: '사내' },
  { value: 'CONFIDENTIAL', label: '대외비' },
  { value: 'TOP_SECRET', label: '극비' },
];

export default function UploadPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [documentType, setDocumentType] = useState('GENERAL');
  const [securityLevel, setSecurityLevel] = useState('INTERNAL');
  const [file, setFile] = useState<File | null>(null);
  const [dragActive, setDragActive] = useState(false);

  const uploadMutation = useMutation({
    mutationFn: () => {
      if (!file) throw new Error('파일을 선택해주세요');
      return documentApi.upload(
        { title, description, documentType, securityLevel },
        file
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['documents'] });
      router.push('/documents');
    },
  });

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDragActive(false);
    const droppedFile = e.dataTransfer.files[0];
    if (droppedFile) {
      setFile(droppedFile);
      if (!title) setTitle(droppedFile.name.replace(/\.[^.]+$/, ''));
    }
  }, [title]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
      if (!title) setTitle(selectedFile.name.replace(/\.[^.]+$/, ''));
    }
  };

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">문서 업로드</h1>

      <div className="space-y-4 rounded-lg border bg-white p-6 shadow-sm">
        {/* 파일 드롭존 */}
        <div
          onDragOver={(e) => { e.preventDefault(); setDragActive(true); }}
          onDragLeave={() => setDragActive(false)}
          onDrop={handleDrop}
          className={`flex flex-col items-center justify-center rounded-lg border-2 border-dashed p-8 transition-colors ${
            dragActive ? 'border-blue-500 bg-blue-50' : 'border-gray-300'
          }`}
        >
          {file ? (
            <div className="flex items-center gap-3">
              <FileText size={24} className="text-blue-600" />
              <div>
                <p className="text-sm font-medium">{file.name}</p>
                <p className="text-xs text-gray-500">
                  {(file.size / (1024 * 1024)).toFixed(2)} MB
                </p>
              </div>
              <button onClick={() => setFile(null)} className="ml-4 text-gray-400 hover:text-red-500">
                <X size={16} />
              </button>
            </div>
          ) : (
            <>
              <Upload size={32} className="mb-2 text-gray-400" />
              <p className="text-sm text-gray-600">파일을 드래그하거나 클릭하여 선택</p>
              <input
                type="file"
                onChange={handleFileChange}
                className="absolute inset-0 cursor-pointer opacity-0"
                style={{ position: 'relative' }}
              />
            </>
          )}
        </div>

        {/* 메타데이터 입력 */}
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">제목 *</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full rounded-md border px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
            placeholder="문서 제목"
          />
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">설명</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
            className="w-full rounded-md border px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
            placeholder="문서 설명 (선택)"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">문서 유형 *</label>
            <select
              value={documentType}
              onChange={(e) => setDocumentType(e.target.value)}
              className="w-full rounded-md border px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
            >
              {DOCUMENT_TYPES.map((t) => (
                <option key={t.value} value={t.value}>{t.label}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">보안 등급</label>
            <select
              value={securityLevel}
              onChange={(e) => setSecurityLevel(e.target.value)}
              className="w-full rounded-md border px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
            >
              {SECURITY_LEVELS.map((s) => (
                <option key={s.value} value={s.value}>{s.label}</option>
              ))}
            </select>
          </div>
        </div>

        {uploadMutation.error && (
          <p className="text-sm text-red-600">
            {uploadMutation.error instanceof Error ? uploadMutation.error.message : '업로드 실패'}
          </p>
        )}

        <div className="flex justify-end gap-3">
          <button
            onClick={() => router.back()}
            className="rounded-md border px-4 py-2 text-sm hover:bg-gray-50"
          >
            취소
          </button>
          <button
            onClick={() => uploadMutation.mutate()}
            disabled={!file || !title || uploadMutation.isPending}
            className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
          >
            {uploadMutation.isPending ? '업로드 중...' : '업로드'}
          </button>
        </div>
      </div>
    </div>
  );
}
