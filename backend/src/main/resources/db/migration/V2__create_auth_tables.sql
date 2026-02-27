-- 인증/권한 테이블 (t_auth_)
SET search_path TO edms;

-- 사용자
CREATE TABLE t_auth_user (
    user_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    department      VARCHAR(100),
    position        VARCHAR(100),
    phone           VARCHAR(20),
    profile_image   VARCHAR(500),
    provider        VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    provider_id     VARCHAR(255),
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE INDEX idx_auth_user_email ON t_auth_user(email);
CREATE INDEX idx_auth_user_department ON t_auth_user(department);

-- 역할
CREATE TABLE t_auth_role (
    role_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_code       VARCHAR(50) NOT NULL UNIQUE,
    role_name       VARCHAR(100) NOT NULL,
    description     VARCHAR(500),
    sort_order      INTEGER NOT NULL DEFAULT 0,
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

-- 권한
CREATE TABLE t_auth_permission (
    permission_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_name VARCHAR(200) NOT NULL,
    description     VARCHAR(500),
    category        VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

-- 사용자-역할 매핑
CREATE TABLE t_auth_user_role (
    user_role_uuid  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_uuid       UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    role_uuid       UUID NOT NULL REFERENCES t_auth_role(role_uuid),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    UNIQUE (user_uuid, role_uuid)
);

-- 역할-권한 매핑
CREATE TABLE t_auth_role_permission (
    role_permission_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_uuid            UUID NOT NULL REFERENCES t_auth_role(role_uuid),
    permission_uuid      UUID NOT NULL REFERENCES t_auth_permission(permission_uuid),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid      UUID,
    UNIQUE (role_uuid, permission_uuid)
);

-- 메뉴
CREATE TABLE t_auth_menu (
    menu_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_uuid     UUID REFERENCES t_auth_menu(menu_uuid),
    menu_code       VARCHAR(50) NOT NULL UNIQUE,
    menu_name       VARCHAR(100) NOT NULL,
    menu_path       VARCHAR(300),
    icon            VARCHAR(50),
    sort_order      INTEGER NOT NULL DEFAULT 0,
    visible_flag    BOOLEAN NOT NULL DEFAULT TRUE,
    permission_uuid UUID REFERENCES t_auth_permission(permission_uuid),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

-- 로그인 이력
CREATE TABLE t_auth_login_history (
    login_history_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_uuid          UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    login_type         VARCHAR(20) NOT NULL,
    ip_address         VARCHAR(45),
    user_agent         VARCHAR(500),
    success_flag       BOOLEAN NOT NULL,
    failure_reason     VARCHAR(200),
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_login_history_user ON t_auth_login_history(user_uuid, created_at DESC);
