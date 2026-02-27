'use client';

import { useRouter } from 'next/navigation';

const TEST_USERS = [
  {
    uuid: '00000000-0000-0000-0000-000000000001',
    email: 'admin@edms.dev',
    name: '관리자',
    role: 'ADMIN',
    department: '시스템',
  },
  {
    uuid: '00000000-0000-0000-0000-000000000002',
    email: 'user1@edms.dev',
    name: '김철수',
    role: 'USER',
    department: '개발팀',
  },
  {
    uuid: '00000000-0000-0000-0000-000000000003',
    email: 'user2@edms.dev',
    name: '이영희',
    role: 'USER',
    department: '마케팅팀',
  },
];

export default function LoginPage() {
  const router = useRouter();

  const handleLogin = (user: typeof TEST_USERS[0]) => {
    if (typeof window !== 'undefined') {
      localStorage.setItem('userUuid', user.uuid);
      localStorage.setItem('userEmail', user.email);
      localStorage.setItem('userName', user.name);
      localStorage.setItem('userRole', user.role);
    }
    router.push('/dashboard');
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

        <div className="space-y-3">
          <p className="text-xs font-medium text-gray-500">테스트 사용자 선택</p>
          {TEST_USERS.map((user) => (
            <button
              key={user.uuid}
              onClick={() => handleLogin(user)}
              className="flex w-full items-center gap-4 rounded-md border px-4 py-3 text-left hover:bg-blue-50 hover:border-blue-300"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-100 text-sm font-bold text-blue-700">
                {user.name[0]}
              </div>
              <div>
                <p className="text-sm font-medium text-gray-900">{user.name}</p>
                <p className="text-xs text-gray-500">
                  {user.department} / {user.role}
                </p>
              </div>
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
