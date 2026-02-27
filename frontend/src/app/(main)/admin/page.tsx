'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { userApi, type UserResponse } from '@/lib/api/auth';
import { Users, Search, Shield, ShieldOff } from 'lucide-react';
import { useState } from 'react';

const ROLE_LABELS: Record<string, { label: string; color: string }> = {
  ADMIN: { label: '관리자', color: 'bg-red-100 text-red-700' },
  MANAGER: { label: '매니저', color: 'bg-blue-100 text-blue-700' },
  USER: { label: '사용자', color: 'bg-green-100 text-green-700' },
  VIEWER: { label: '뷰어', color: 'bg-gray-100 text-gray-700' },
};

export default function AdminPage() {
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['users', searchKeyword],
    queryFn: () => userApi.list({ keyword: searchKeyword || undefined, size: 50 }),
    select: (res) => res.data.data,
  });

  const toggleMutation = useMutation({
    mutationFn: (uuid: string) => userApi.toggleActive(uuid),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
  });

  const users = data?.content ?? [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">사용자 관리</h1>
        <div className="flex items-center gap-2">
          <Users size={20} className="text-gray-400" />
          <span className="text-sm text-gray-500">
            총 {data?.totalElements ?? 0}명
          </span>
        </div>
      </div>

      {/* 검색 */}
      <div className="flex gap-2">
        <div className="relative flex-1">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="이름 또는 이메일로 검색..."
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

      {/* 사용자 목록 */}
      <div className="rounded-lg border bg-white shadow-sm">
        {isLoading ? (
          <div className="p-8 text-center text-sm text-gray-500">로딩 중...</div>
        ) : users.length === 0 ? (
          <div className="p-8 text-center text-sm text-gray-500">사용자가 없습니다.</div>
        ) : (
          <table className="w-full">
            <thead>
              <tr className="border-b bg-gray-50 text-left text-xs font-medium text-gray-500">
                <th className="px-4 py-3">이름</th>
                <th className="px-4 py-3">이메일</th>
                <th className="px-4 py-3">부서</th>
                <th className="px-4 py-3">직위</th>
                <th className="px-4 py-3">역할</th>
                <th className="px-4 py-3">상태</th>
                <th className="px-4 py-3">마지막 로그인</th>
                <th className="px-4 py-3">관리</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user: UserResponse) => (
                <tr key={user.userUuid} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-3 text-sm font-medium">{user.name}</td>
                  <td className="px-4 py-3 text-sm text-gray-500">{user.email}</td>
                  <td className="px-4 py-3 text-sm text-gray-600">{user.department || '-'}</td>
                  <td className="px-4 py-3 text-sm text-gray-600">{user.position || '-'}</td>
                  <td className="px-4 py-3">
                    <div className="flex gap-1">
                      {user.roles.map((role) => {
                        const info = ROLE_LABELS[role] || { label: role, color: 'bg-gray-100' };
                        return (
                          <span key={role} className={`rounded-full px-2 py-0.5 text-xs font-medium ${info.color}`}>
                            {info.label}
                          </span>
                        );
                      })}
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${
                      user.activeFlag ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                    }`}>
                      {user.activeFlag ? '활성' : '비활성'}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-xs text-gray-500">
                    {user.lastLoginAt
                      ? new Date(user.lastLoginAt).toLocaleString('ko-KR')
                      : '-'}
                  </td>
                  <td className="px-4 py-3">
                    <button
                      onClick={() => toggleMutation.mutate(user.userUuid)}
                      className="rounded-md p-1 hover:bg-gray-100"
                      title={user.activeFlag ? '비활성화' : '활성화'}
                    >
                      {user.activeFlag ? (
                        <ShieldOff size={16} className="text-red-500" />
                      ) : (
                        <Shield size={16} className="text-green-500" />
                      )}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
