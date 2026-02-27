'use client';

import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { authApi } from '@/lib/api/auth';

const TEST_USERS = [
  {
    email: 'admin@edms.dev',
    name: '관리자',
    role: 'ADMIN',
    department: '시스템',
  },
  {
    email: 'user1@edms.dev',
    name: '김철수',
    role: 'USER',
    department: '개발팀',
  },
  {
    email: 'user2@edms.dev',
    name: '이영희',
    role: 'USER',
    department: '마케팅팀',
  },
];

export default function LoginPage() {
  const router = useRouter();
  const [loading, setLoading] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async (email: string) => {
    setLoading(email);
    setError(null);
    try {
      const res = await authApi.login(email);
      const { accessToken, refreshToken, user } = res.data.data;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('userUuid', user.userUuid);
      localStorage.setItem('userName', user.name);
      localStorage.setItem('userEmail', user.email);
      localStorage.setItem('userRoles', JSON.stringify(user.roles));
      localStorage.setItem('userPermissions', JSON.stringify(user.permissions));

      router.push('/dashboard');
    } catch (err) {
      setError(err instanceof Error ? err.message : '로그인 실패');
    } finally {
      setLoading(null);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <div className="w-full max-w-md rounded-lg border bg-white p-8 shadow-sm">
        <h1 className="mb-2 text-center text-2xl font-bold text-gray-900">
          AI-EDMS
        </h1>
        <p className="mb-8 text-center text-sm text-gray-500">
          전자문서관리시스템
        </p>

        {error && (
          <div className="mb-4 rounded-md bg-red-50 p-3 text-sm text-red-600">
            {error}
          </div>
        )}

        <div className="space-y-3">
          <p className="text-xs font-medium text-gray-500">테스트 사용자 선택</p>
          {TEST_USERS.map((user) => (
            <button
              key={user.email}
              onClick={() => handleLogin(user.email)}
              disabled={loading !== null}
              className="flex w-full items-center gap-4 rounded-md border px-4 py-3 text-left hover:bg-blue-50 hover:border-blue-300 disabled:opacity-50"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-100 text-sm font-bold text-blue-700">
                {user.name[0]}
              </div>
              <div className="flex-1">
                <p className="text-sm font-medium text-gray-900">{user.name}</p>
                <p className="text-xs text-gray-500">
                  {user.department} / {user.role}
                </p>
              </div>
              {loading === user.email && (
                <span className="text-xs text-blue-600">로그인 중...</span>
              )}
            </button>
          ))}
        </div>

        <div className="mt-6">
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <span className="w-full border-t" />
            </div>
            <div className="relative flex justify-center text-xs uppercase">
              <span className="bg-white px-2 text-gray-500">Phase 3에서 추가</span>
            </div>
          </div>

          <div className="mt-4 space-y-2">
            <button
              disabled
              className="w-full rounded-md border px-4 py-3 text-sm font-medium text-gray-400"
            >
              Azure AD SSO 로그인
            </button>
            <button
              disabled
              className="w-full rounded-md border px-4 py-3 text-sm font-medium text-gray-400"
            >
              Magic Link 로그인
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
