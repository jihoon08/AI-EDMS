'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  LayoutDashboard,
  FileText,
  FolderOpen,
  GitPullRequest,
  Search,
  Bot,
  FileType,
  Settings,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { useState } from 'react';

const menuItems = [
  { code: 'DASHBOARD', name: '대시보드', path: '/dashboard', icon: LayoutDashboard },
  { code: 'DOCUMENTS', name: '문서 관리', path: '/documents', icon: FileText },
  { code: 'FOLDERS', name: '폴더 관리', path: '/folders', icon: FolderOpen },
  { code: 'WORKFLOWS', name: '결재 관리', path: '/workflows', icon: GitPullRequest },
  { code: 'SEARCH', name: '검색', path: '/search', icon: Search },
  { code: 'AI', name: 'AI 어시스턴트', path: '/ai', icon: Bot },
  { code: 'TEMPLATES', name: '문서 템플릿', path: '/templates', icon: FileType },
];

const adminItems = [
  { code: 'ADMIN', name: '관리자', path: '/admin', icon: Settings },
];

export function Sidebar() {
  const pathname = usePathname();
  const [collapsed, setCollapsed] = useState(false);

  return (
    <aside
      className={cn(
        'flex flex-col border-r bg-white transition-all duration-300',
        collapsed ? 'w-16' : 'w-60'
      )}
    >
      {/* 로고 */}
      <div className="flex h-14 items-center border-b px-4">
        {!collapsed && (
          <Link href="/dashboard" className="text-lg font-bold text-gray-900">
            AI-EDMS
          </Link>
        )}
        <button
          onClick={() => setCollapsed(!collapsed)}
          className={cn(
            'rounded-md p-1 hover:bg-gray-100',
            collapsed ? 'mx-auto' : 'ml-auto'
          )}
        >
          {collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
        </button>
      </div>

      {/* 메뉴 */}
      <nav className="flex-1 space-y-1 px-2 py-4">
        {menuItems.map((item) => {
          const isActive = pathname.startsWith(item.path);
          return (
            <Link
              key={item.code}
              href={item.path}
              className={cn(
                'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-blue-50 text-blue-700'
                  : 'text-gray-700 hover:bg-gray-100'
              )}
              title={collapsed ? item.name : undefined}
            >
              <item.icon size={20} />
              {!collapsed && <span>{item.name}</span>}
            </Link>
          );
        })}

        <div className="my-4 border-t" />

        {adminItems.map((item) => {
          const isActive = pathname.startsWith(item.path);
          return (
            <Link
              key={item.code}
              href={item.path}
              className={cn(
                'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-blue-50 text-blue-700'
                  : 'text-gray-700 hover:bg-gray-100'
              )}
              title={collapsed ? item.name : undefined}
            >
              <item.icon size={20} />
              {!collapsed && <span>{item.name}</span>}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
