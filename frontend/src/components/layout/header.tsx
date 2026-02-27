'use client';

import { Bell, User } from 'lucide-react';

export function Header() {
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

        {/* 사용자 */}
        <button className="flex items-center gap-2 rounded-md px-3 py-2 hover:bg-gray-100">
          <User size={20} className="text-gray-600" />
          <span className="text-sm text-gray-700">사용자</span>
        </button>
      </div>
    </header>
  );
}
