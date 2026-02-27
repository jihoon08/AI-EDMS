-- 테스트 사용자 시드 데이터
SET search_path TO edms;

INSERT INTO t_auth_user (user_uuid, email, name, department, position, active_flag, provider, created_at)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'admin@edms.dev', '관리자', '시스템', '관리자', true, 'LOCAL', NOW()),
    ('00000000-0000-0000-0000-000000000002', 'user1@edms.dev', '김철수', '개발팀', '팀장', true, 'LOCAL', NOW()),
    ('00000000-0000-0000-0000-000000000003', 'user2@edms.dev', '이영희', '마케팅팀', '대리', true, 'LOCAL', NOW())
ON CONFLICT DO NOTHING;

-- 관리자에게 ADMIN 역할 부여
INSERT INTO t_auth_user_role (user_role_uuid, user_uuid, role_uuid, created_at)
SELECT gen_random_uuid(), '00000000-0000-0000-0000-000000000001', role_uuid, NOW()
FROM t_auth_role WHERE role_code = 'ADMIN'
ON CONFLICT DO NOTHING;

-- 김철수에게 USER 역할 부여
INSERT INTO t_auth_user_role (user_role_uuid, user_uuid, role_uuid, created_at)
SELECT gen_random_uuid(), '00000000-0000-0000-0000-000000000002', role_uuid, NOW()
FROM t_auth_role WHERE role_code = 'USER'
ON CONFLICT DO NOTHING;

-- 이영희에게 USER 역할 부여
INSERT INTO t_auth_user_role (user_role_uuid, user_uuid, role_uuid, created_at)
SELECT gen_random_uuid(), '00000000-0000-0000-0000-000000000003', role_uuid, NOW()
FROM t_auth_role WHERE role_code = 'USER'
ON CONFLICT DO NOTHING;
