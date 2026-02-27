'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { folderApi, type FolderTreeNode } from '@/lib/api/folders';
import { type DocumentResponse } from '@/lib/api/documents';
import { FolderOpen, FolderPlus, ChevronRight, ChevronDown, FileText } from 'lucide-react';
import { useState } from 'react';
import Link from 'next/link';

function TreeItem({
  node,
  selectedId,
  onSelect,
}: {
  node: FolderTreeNode;
  selectedId: string | null;
  onSelect: (id: string) => void;
}) {
  const [expanded, setExpanded] = useState(node.depth === 0);
  const hasChildren = node.children.length > 0;
  const isSelected = selectedId === node.folderUuid;

  return (
    <div>
      <div
        className={`flex cursor-pointer items-center gap-1 rounded-md px-2 py-1.5 text-sm hover:bg-gray-100 ${
          isSelected ? 'bg-blue-50 text-blue-700' : ''
        }`}
        style={{ paddingLeft: `${node.depth * 16 + 8}px` }}
        onClick={() => {
          onSelect(node.folderUuid);
          if (hasChildren) setExpanded(!expanded);
        }}
      >
        {hasChildren ? (
          expanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />
        ) : (
          <span className="w-[14px]" />
        )}
        <FolderOpen size={16} className={isSelected ? 'text-blue-600' : 'text-yellow-500'} />
        <span className="truncate">{node.folderName}</span>
      </div>
      {expanded && hasChildren && (
        <div>
          {node.children.map((child) => (
            <TreeItem key={child.folderUuid} node={child} selectedId={selectedId} onSelect={onSelect} />
          ))}
        </div>
      )}
    </div>
  );
}

export default function FoldersPage() {
  const queryClient = useQueryClient();
  const [selectedFolderId, setSelectedFolderId] = useState<string | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');

  const { data: tree, isLoading } = useQuery({
    queryKey: ['folder-tree'],
    queryFn: () => folderApi.tree(),
    select: (res) => res.data.data,
  });

  const { data: documents } = useQuery({
    queryKey: ['folder-documents', selectedFolderId],
    queryFn: () => folderApi.documents(selectedFolderId!, { size: 50 }),
    select: (res) => res.data.data,
    enabled: !!selectedFolderId,
  });

  const createMutation = useMutation({
    mutationFn: () =>
      folderApi.create({
        folderName: newFolderName,
        parentUuid: selectedFolderId || undefined,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['folder-tree'] });
      setNewFolderName('');
      setShowCreateForm(false);
    },
  });

  return (
    <div className="flex h-full gap-6">
      {/* 폴더 트리 */}
      <div className="w-72 shrink-0 rounded-lg border bg-white p-4 shadow-sm">
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-sm font-semibold text-gray-900">폴더</h2>
          <button
            onClick={() => setShowCreateForm(!showCreateForm)}
            className="rounded-md p-1 hover:bg-gray-100"
            title="폴더 생성"
          >
            <FolderPlus size={16} className="text-gray-500" />
          </button>
        </div>

        {showCreateForm && (
          <div className="mb-3 flex gap-2">
            <input
              type="text"
              value={newFolderName}
              onChange={(e) => setNewFolderName(e.target.value)}
              placeholder="폴더 이름"
              className="flex-1 rounded-md border px-2 py-1 text-sm"
              onKeyDown={(e) => e.key === 'Enter' && newFolderName && createMutation.mutate()}
            />
            <button
              onClick={() => createMutation.mutate()}
              disabled={!newFolderName}
              className="rounded-md bg-blue-600 px-2 py-1 text-xs text-white disabled:opacity-50"
            >
              생성
            </button>
          </div>
        )}

        <div className="space-y-0.5">
          {isLoading ? (
            <p className="text-xs text-gray-500">로딩 중...</p>
          ) : tree && tree.length > 0 ? (
            tree.map((node) => (
              <TreeItem
                key={node.folderUuid}
                node={node}
                selectedId={selectedFolderId}
                onSelect={setSelectedFolderId}
              />
            ))
          ) : (
            <p className="text-xs text-gray-500">폴더가 없습니다.</p>
          )}
        </div>
      </div>

      {/* 문서 목록 */}
      <div className="flex-1 rounded-lg border bg-white p-6 shadow-sm">
        {selectedFolderId ? (
          <>
            <h2 className="mb-4 text-lg font-semibold text-gray-900">문서 목록</h2>
            {documents && documents.content.length > 0 ? (
              <div className="space-y-2">
                {documents.content.map((doc: DocumentResponse) => (
                  <Link
                    key={doc.documentUuid}
                    href={`/documents/${doc.documentUuid}`}
                    className="flex items-center gap-3 rounded-md border p-3 hover:bg-gray-50"
                  >
                    <FileText size={20} className="text-blue-500" />
                    <div>
                      <p className="text-sm font-medium">{doc.title}</p>
                      <p className="text-xs text-gray-500">{doc.documentNumber} - {doc.fileName}</p>
                    </div>
                  </Link>
                ))}
              </div>
            ) : (
              <p className="text-sm text-gray-500">이 폴더에 문서가 없습니다.</p>
            )}
          </>
        ) : (
          <div className="flex h-full items-center justify-center">
            <p className="text-sm text-gray-500">폴더를 선택하면 문서 목록이 표시됩니다.</p>
          </div>
        )}
      </div>
    </div>
  );
}
