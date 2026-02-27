'use client';

import { Bell, LogOut } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

export function Header() {
  const router = useRouter();
  const [userName, setUserName] = useState('');
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    if (typeof window !== 'undefined') {
      setUserName(localStorage.getItem('userName') || '');
      const roles = localStorage.getItem('userRoles');
      if (roles) {
        try {
          const parsed = JSON.parse(roles);
          setUserRole(parsed[0] || '');
        } catch {
          setUserRole(localStorage.getItem('userRole') || '');
        }
      }
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userUuid');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userRoles');
    localStorage.removeItem('userPermissions');
    localStorage.removeItem('userRole');
    router.push('/login');
  };

  return (
    <header className="flex h-14 items-center justify-between border-b bg-white px-6">
      <div className="flex items-center gap-4">
        <h1 className="text-sm font-medium text-gray-500">
          전자문서관리시스템
        </h1>
      </div>

      <div className="flex items-center gap-4">
        {/* 알림 */}
        <button className="relative rounded-md p-2 hover:bg-gray-100">
          <Bell size={20} className="text-gray-600" />
        </button>

        {/* 사용자 정보 */}
        <div className="flex items-center gap-3">
          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-xs font-bold text-blue-700">
            {userName ? userName[0] : '?'}
          </div>
          <div className="text-right">
            <p className="text-sm font-medium text-gray-700">{userName || '사용자'}</p>
            {userRole && (
              <p className="text-xs text-gray-400">{userRole}</p>
            )}
          </div>
          <button
            onClick={handleLogout}
            className="ml-2 rounded-md p-1.5 hover:bg-gray-100"
            title="로그아웃"
          >
            <LogOut size={16} className="text-gray-500" />
          </button>
        </div>
      </div>
    </header>
  );
}
