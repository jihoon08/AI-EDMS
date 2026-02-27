-- 초기 시드 데이터
SET search_path TO edms;

-- 기본 역할
INSERT INTO t_auth_role (role_code, role_name, description, sort_order) VALUES
('ADMIN', '시스템 관리자', '시스템 전체 관리 권한', 1),
('MANAGER', '부서 관리자', '부서 내 문서/결재 관리', 2),
('USER', '일반 사용자', '문서 조회/등록/결재', 3),
('VIEWER', '조회 전용', '문서 조회만 가능', 4);

-- 기본 권한
INSERT INTO t_auth_permission (permission_code, permission_name, category) VALUES
-- 문서
('DOC_CREATE', '문서 등록', 'DOCUMENT'),
('DOC_READ', '문서 조회', 'DOCUMENT'),
('DOC_UPDATE', '문서 수정', 'DOCUMENT'),
('DOC_DELETE', '문서 삭제', 'DOCUMENT'),
('DOC_DOWNLOAD', '문서 다운로드', 'DOCUMENT'),
('DOC_SHARE', '문서 공유', 'DOCUMENT'),
-- 폴더
('FOLDER_CREATE', '폴더 생성', 'FOLDER'),
('FOLDER_READ', '폴더 조회', 'FOLDER'),
('FOLDER_UPDATE', '폴더 수정', 'FOLDER'),
('FOLDER_DELETE', '폴더 삭제', 'FOLDER'),
('FOLDER_PERMISSION', '폴더 권한 관리', 'FOLDER'),
-- 결재
('WORKFLOW_CREATE', '결재 요청', 'WORKFLOW'),
('WORKFLOW_APPROVE', '결재 승인/반려', 'WORKFLOW'),
('WORKFLOW_TEMPLATE', '결재 템플릿 관리', 'WORKFLOW'),
-- AI
('AI_SEARCH', 'AI 검색', 'AI'),
('AI_QA', 'AI Q&A', 'AI'),
-- 관리자
('ADMIN_USER', '사용자 관리', 'ADMIN'),
('ADMIN_ROLE', '역할/권한 관리', 'ADMIN'),
('ADMIN_SYSTEM', '시스템 설정', 'ADMIN'),
('ADMIN_AUDIT', '감사 로그 조회', 'ADMIN'),
('ADMIN_BATCH', '배치 관리', 'ADMIN'),
('ADMIN_TEMPLATE', '문서 템플릿 관리', 'ADMIN'),
('ADMIN_RETENTION', '보존정책 관리', 'ADMIN');

-- ADMIN 역할에 모든 권한 할당
INSERT INTO t_auth_role_permission (role_uuid, permission_uuid)
SELECT r.role_uuid, p.permission_uuid
FROM t_auth_role r, t_auth_permission p
WHERE r.role_code = 'ADMIN';

-- USER 역할에 기본 권한
INSERT INTO t_auth_role_permission (role_uuid, permission_uuid)
SELECT r.role_uuid, p.permission_uuid
FROM t_auth_role r, t_auth_permission p
WHERE r.role_code = 'USER'
  AND p.permission_code IN ('DOC_CREATE', 'DOC_READ', 'DOC_UPDATE', 'DOC_DOWNLOAD', 'DOC_SHARE',
                            'FOLDER_READ', 'WORKFLOW_CREATE', 'WORKFLOW_APPROVE',
                            'AI_SEARCH', 'AI_QA');

-- VIEWER 역할에 조회 권한
INSERT INTO t_auth_role_permission (role_uuid, permission_uuid)
SELECT r.role_uuid, p.permission_uuid
FROM t_auth_role r, t_auth_permission p
WHERE r.role_code = 'VIEWER'
  AND p.permission_code IN ('DOC_READ', 'DOC_DOWNLOAD', 'FOLDER_READ', 'AI_SEARCH');

-- 기본 메뉴
INSERT INTO t_auth_menu (menu_code, menu_name, menu_path, icon, sort_order) VALUES
('DASHBOARD', '대시보드', '/dashboard', 'LayoutDashboard', 1),
('DOCUMENTS', '문서 관리', '/documents', 'FileText', 2),
('FOLDERS', '폴더 관리', '/folders', 'FolderOpen', 3),
('WORKFLOWS', '결재 관리', '/workflows', 'GitPullRequest', 4),
('SEARCH', '검색', '/search', 'Search', 5),
('AI', 'AI 어시스턴트', '/ai', 'Bot', 6),
('TEMPLATES', '문서 템플릿', '/templates', 'FileTemplate', 7);

INSERT INTO t_auth_menu (menu_code, menu_name, menu_path, icon, sort_order, parent_uuid) VALUES
('ADMIN_USERS', '사용자 관리', '/admin/users', 'Users', 1,
 (SELECT menu_uuid FROM t_auth_menu WHERE menu_code = 'DASHBOARD')),
('ADMIN_ROLES', '역할 관리', '/admin/roles', 'Shield', 2,
 (SELECT menu_uuid FROM t_auth_menu WHERE menu_code = 'DASHBOARD'));

-- 관리자 메뉴 (별도 최상위)
UPDATE t_auth_menu SET parent_uuid = NULL WHERE menu_code IN ('ADMIN_USERS', 'ADMIN_ROLES');

INSERT INTO t_auth_menu (menu_code, menu_name, menu_path, icon, sort_order) VALUES
('ADMIN', '관리자', '/admin', 'Settings', 100);

UPDATE t_auth_menu SET parent_uuid = (SELECT menu_uuid FROM t_auth_menu WHERE menu_code = 'ADMIN')
WHERE menu_code IN ('ADMIN_USERS', 'ADMIN_ROLES');

-- 기본 보존정책
INSERT INTO t_core_retention_policy (policy_name, document_type, retention_years, description) VALUES
('계약서 보존정책', 'CONTRACT', 10, '계약서는 10년 보존'),
('보고서 보존정책', 'REPORT', 5, '보고서는 5년 보존'),
('매뉴얼 보존정책', 'MANUAL', 3, '매뉴얼은 3년 보존'),
('일반문서 보존정책', 'GENERAL', 1, '일반 문서는 1년 보존'),
('규정/지침 보존정책', 'REGULATION', -1, '규정/지침은 영구 보존');

-- 기본 시스템 설정
INSERT INTO t_sys_config (config_key, config_value, description, category) VALUES
('MAX_FILE_SIZE_MB', '100', '최대 파일 크기 (MB)', 'STORAGE'),
('ALLOWED_FILE_TYPES', 'pdf,doc,docx,xls,xlsx,ppt,pptx,txt,hwp,hwpx,jpg,jpeg,png,gif,zip', '허용 파일 유형', 'STORAGE'),
('AI_AUTO_CLASSIFY', 'true', 'AI 자동 분류 활성화', 'AI'),
('AI_AUTO_SUMMARIZE', 'true', 'AI 자동 요약 활성화', 'AI'),
('AI_AUTO_TAG', 'true', 'AI 자동 태그 활성화', 'AI'),
('AI_AUTO_OCR', 'true', 'AI OCR 자동 실행', 'AI'),
('WATERMARK_ENABLED', 'true', '워터마크 활성화', 'SECURITY'),
('SESSION_TIMEOUT_MINUTES', '480', '세션 타임아웃 (분)', 'SECURITY'),
('DOCUMENT_NUMBER_PREFIX', 'DOC', '문서 번호 접두사', 'DOCUMENT'),
('RETENTION_ALERT_DAYS', '30', '보존기간 만료 알림 (일)', 'RETENTION');

-- 배치 작업 정의
INSERT INTO t_batch_job (job_code, job_name, description, job_class) VALUES
('AI_PROCESS', 'AI 문서 처리', 'AI 큐의 대기 작업 처리', 'com.edms.batch.job.AiProcessingJob'),
('RETENTION_CHECK', '보존기간 만료 점검', '보존기간 만료 문서 자동 보관', 'com.edms.batch.job.RetentionCheckJob'),
('ACCESS_EXPIRE', '접근 권한 만료 처리', '임시 접근 권한 자동 만료', 'com.edms.batch.job.AccessExpireJob'),
('LOCK_CLEANUP', '문서 잠금 정리', '만료된 문서 잠금 해제', 'com.edms.batch.job.LockCleanupJob'),
('DAILY_STATS', '일일 통계 집계', '문서/AI 통계 집계', 'com.edms.batch.job.DailyStatsJob');

-- 알림 템플릿
INSERT INTO t_noti_template (template_code, channel_type, subject, body) VALUES
('APPROVAL_REQUEST', 'EMAIL', '결재 요청: ${title}', '${requesterName}님이 결재를 요청했습니다.\n\n문서: ${documentTitle}\n결재 단계: ${stepName}\n\n결재 링크: ${approvalLink}'),
('APPROVAL_APPROVED', 'EMAIL', '결재 승인: ${title}', '${approverName}님이 결재를 승인했습니다.\n\n문서: ${documentTitle}\n단계: ${stepName}'),
('APPROVAL_REJECTED', 'EMAIL', '결재 반려: ${title}', '${approverName}님이 결재를 반려했습니다.\n\n문서: ${documentTitle}\n사유: ${reason}'),
('DOCUMENT_SHARED', 'EMAIL', '문서 공유: ${documentTitle}', '${sharerName}님이 문서를 공유했습니다.\n\n문서: ${documentTitle}\n메시지: ${message}'),
('ACCESS_REQUEST', 'EMAIL', '접근 권한 요청: ${targetName}', '${requesterName}님이 접근 권한을 요청했습니다.\n\n대상: ${targetName}\n사유: ${reason}'),
('ACCESS_APPROVED', 'EMAIL', '접근 권한 승인', '접근 권한이 승인되었습니다.\n\n대상: ${targetName}\n기간: ${grantedUntil}'),
('RETENTION_EXPIRING', 'EMAIL', '보존기간 만료 예정: ${documentTitle}', '다음 문서의 보존기간이 ${daysRemaining}일 후 만료됩니다.\n\n문서: ${documentTitle}\n만료일: ${expiresAt}');

-- 루트 폴더
INSERT INTO t_core_folder (folder_name, materialized_path, depth, description)
VALUES ('전체 문서', '/', 0, 'EDMS 루트 폴더');
