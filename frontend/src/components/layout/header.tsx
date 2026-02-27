'use client';

import { Bell, LogOut } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useEffect, useState, useRef } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { notificationApi, type AlertResponse } from '@/lib/api/workflow';

export function Header() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [userName, setUserName] = useState('');
  const [userRole, setUserRole] = useState('');
  const [showNotifications, setShowNotifications] = useState(false);
  const notifRef = useRef<HTMLDivElement>(null);

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

  // 바깥 클릭 시 드롭다운 닫기
  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      if (notifRef.current && !notifRef.current.contains(e.target as Node)) {
        setShowNotifications(false);
      }
    };
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  const { data: unreadCount } = useQuery({
    queryKey: ['notifications-unread-count'],
    queryFn: () => notificationApi.getUnreadCount(),
    select: (res) => res.data.data.count,
    refetchInterval: 30000,
  });

  const { data: alerts } = useQuery({
    queryKey: ['notifications-recent'],
    queryFn: () => notificationApi.getAlerts({ size: 10 }),
    select: (res) => res.data.data?.content ?? [],
    enabled: showNotifications,
  });

  const markReadMutation = useMutation({
    mutationFn: (uuid: string) => notificationApi.markAsRead(uuid),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications-unread-count'] });
      queryClient.invalidateQueries({ queryKey: ['notifications-recent'] });
    },
  });

  const markAllReadMutation = useMutation({
    mutationFn: () => notificationApi.markAllAsRead(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications-unread-count'] });
      queryClient.invalidateQueries({ queryKey: ['notifications-recent'] });
    },
  });

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
        <div className="relative" ref={notifRef}>
          <button
            className="relative rounded-md p-2 hover:bg-gray-100"
            onClick={() => setShowNotifications(!showNotifications)}
          >
            <Bell size={20} className="text-gray-600" />
            {(unreadCount ?? 0) > 0 && (
              <span className="absolute -top-0.5 -right-0.5 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white">
                {unreadCount! > 9 ? '9+' : unreadCount}
              </span>
            )}
          </button>

          {/* 알림 드롭다운 */}
          {showNotifications && (
            <div className="absolute right-0 top-full mt-2 w-80 rounded-lg border bg-white shadow-lg z-50">
              <div className="flex items-center justify-between border-b px-4 py-3">
                <span className="text-sm font-medium">알림</span>
                {(unreadCount ?? 0) > 0 && (
                  <button
                    onClick={() => markAllReadMutation.mutate()}
                    className="text-xs text-blue-600 hover:underline"
                  >
                    전체 읽음
                  </button>
                )}
              </div>
              <div className="max-h-80 overflow-y-auto">
                {!alerts || alerts.length === 0 ? (
                  <div className="p-4 text-center text-xs text-gray-400">
                    알림이 없습니다
                  </div>
                ) : (
                  alerts.map((alert: AlertResponse) => (
                    <div
                      key={alert.alertUuid}
                      className={`border-b px-4 py-3 cursor-pointer hover:bg-gray-50 ${
                        !alert.readFlag ? 'bg-blue-50/50' : ''
                      }`}
                      onClick={() => {
                        if (!alert.readFlag) {
                          markReadMutation.mutate(alert.alertUuid);
                        }
                        if (alert.link) {
                          router.push(alert.link);
                          setShowNotifications(false);
                        }
                      }}
                    >
                      <p className="text-sm font-medium text-gray-900">{alert.title}</p>
                      {alert.message && (
                        <p className="mt-0.5 text-xs text-gray-500 line-clamp-2">{alert.message}</p>
                      )}
                      <p className="mt-1 text-[10px] text-gray-400">
                        {new Date(alert.createdAt).toLocaleString('ko-KR')}
                      </p>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

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
