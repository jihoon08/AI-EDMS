-- 핵심 도메인 테이블 (문서, 폴더, 결재, 공유, 템플릿, 보존)
SET search_path TO edms;

-- ==================== 폴더 ====================
CREATE TABLE t_core_folder (
    folder_uuid         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_uuid         UUID REFERENCES t_core_folder(folder_uuid),
    folder_name         VARCHAR(255) NOT NULL,
    materialized_path   VARCHAR(2000) NOT NULL DEFAULT '/',
    depth               INTEGER NOT NULL DEFAULT 0,
    description         VARCHAR(500),
    owner_uuid          UUID REFERENCES t_auth_user(user_uuid),
    inherit_permission  BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order          INTEGER NOT NULL DEFAULT 0,
    deleted_flag        BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP WITH TIME ZONE,
    version_seq         BIGINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid     UUID,
    updated_at          TIMESTAMP WITH TIME ZONE,
    updated_by_uuid     UUID
);

CREATE INDEX idx_folder_parent ON t_core_folder(parent_uuid) WHERE deleted_flag = FALSE;
CREATE INDEX idx_folder_path ON t_core_folder(materialized_path);

-- 폴더 권한
CREATE TABLE t_core_folder_permission (
    folder_permission_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    folder_uuid            UUID NOT NULL REFERENCES t_core_folder(folder_uuid),
    grantee_type           VARCHAR(20) NOT NULL,
    grantee_uuid           UUID NOT NULL,
    permission_level       VARCHAR(20) NOT NULL,
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid        UUID,
    UNIQUE (folder_uuid, grantee_type, grantee_uuid)
);

-- ==================== 문서 ====================
CREATE TABLE t_core_document (
    document_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_number     VARCHAR(30) NOT NULL UNIQUE,
    title               VARCHAR(500) NOT NULL,
    description         TEXT,
    document_type       VARCHAR(50) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    security_level      VARCHAR(20) NOT NULL DEFAULT 'INTERNAL',
    folder_uuid         UUID REFERENCES t_core_folder(folder_uuid),
    owner_uuid          UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    current_version     INTEGER NOT NULL DEFAULT 1,
    file_name           VARCHAR(500) NOT NULL,
    file_size           BIGINT NOT NULL,
    content_type        VARCHAR(200) NOT NULL,
    storage_key         VARCHAR(1000) NOT NULL,
    extracted_text      TEXT,
    search_vector       TSVECTOR,
    retention_period    VARCHAR(20),
    retention_expires_at TIMESTAMP WITH TIME ZONE,
    deleted_flag        BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMP WITH TIME ZONE,
    version_seq         BIGINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid     UUID,
    updated_at          TIMESTAMP WITH TIME ZONE,
    updated_by_uuid     UUID
);

CREATE INDEX idx_document_folder ON t_core_document(folder_uuid) WHERE deleted_flag = FALSE;
CREATE INDEX idx_document_owner ON t_core_document(owner_uuid);
CREATE INDEX idx_document_status ON t_core_document(status) WHERE deleted_flag = FALSE;
CREATE INDEX idx_document_type ON t_core_document(document_type) WHERE deleted_flag = FALSE;
CREATE INDEX idx_document_number ON t_core_document(document_number);
CREATE INDEX idx_document_search ON t_core_document USING GIN(search_vector);
CREATE INDEX idx_document_retention ON t_core_document(retention_expires_at) WHERE retention_expires_at IS NOT NULL AND deleted_flag = FALSE;

-- 문서 버전
CREATE TABLE t_core_document_version (
    version_uuid    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    version_number  INTEGER NOT NULL,
    file_name       VARCHAR(500) NOT NULL,
    file_size       BIGINT NOT NULL,
    content_type    VARCHAR(200) NOT NULL,
    storage_key     VARCHAR(1000) NOT NULL,
    change_summary  VARCHAR(1000),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    UNIQUE (document_uuid, version_number)
);

CREATE INDEX idx_document_version_doc ON t_core_document_version(document_uuid);

-- 문서 메타데이터
CREATE TABLE t_core_document_metadata (
    metadata_uuid   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    meta_key        VARCHAR(100) NOT NULL,
    meta_value      TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID,
    UNIQUE (document_uuid, meta_key)
);

-- 문서 태그
CREATE TABLE t_core_document_tag (
    tag_uuid        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    tag_name        VARCHAR(100) NOT NULL,
    tag_source      VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    confidence      DOUBLE PRECISION,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    UNIQUE (document_uuid, tag_name)
);

CREATE INDEX idx_document_tag_name ON t_core_document_tag(tag_name);

-- 문서 댓글
CREATE TABLE t_core_document_comment (
    comment_uuid    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    parent_uuid     UUID REFERENCES t_core_document_comment(comment_uuid),
    content         TEXT NOT NULL,
    page_number     INTEGER,
    position_x      DOUBLE PRECISION,
    position_y      DOUBLE PRECISION,
    deleted_flag    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE INDEX idx_comment_document ON t_core_document_comment(document_uuid) WHERE deleted_flag = FALSE;

-- 문서 관계
CREATE TABLE t_core_document_relation (
    relation_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_document_uuid UUID NOT NULL REFERENCES t_core_document(document_uuid),
    target_document_uuid UUID NOT NULL REFERENCES t_core_document(document_uuid),
    relation_type       VARCHAR(20) NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid     UUID,
    UNIQUE (source_document_uuid, target_document_uuid, relation_type)
);

-- 문서 권한
CREATE TABLE t_core_document_permission (
    doc_permission_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid       UUID NOT NULL REFERENCES t_core_document(document_uuid),
    grantee_type        VARCHAR(20) NOT NULL,
    grantee_uuid        UUID NOT NULL,
    permission_level    VARCHAR(20) NOT NULL,
    expires_at          TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid     UUID,
    UNIQUE (document_uuid, grantee_type, grantee_uuid)
);

-- 문서 잠금
CREATE TABLE t_core_document_lock (
    lock_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL UNIQUE REFERENCES t_core_document(document_uuid),
    locked_by_uuid  UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    lock_type       VARCHAR(20) NOT NULL DEFAULT 'EDIT',
    locked_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 즐겨찾기
CREATE TABLE t_core_favorite (
    favorite_uuid   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_uuid       UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (user_uuid, document_uuid)
);

-- 최근 접근
CREATE TABLE t_core_recent_access (
    access_uuid     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_uuid       UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    access_type     VARCHAR(20) NOT NULL DEFAULT 'VIEW',
    accessed_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_recent_access_user ON t_core_recent_access(user_uuid, accessed_at DESC);

-- ==================== 결재 ====================

-- 결재 템플릿
CREATE TABLE t_core_workflow_template (
    template_uuid   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_name   VARCHAR(200) NOT NULL,
    description     VARCHAR(500),
    document_type   VARCHAR(50),
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    version_seq     BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

-- 결재 템플릿 단계
CREATE TABLE t_core_workflow_template_step (
    step_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_uuid   UUID NOT NULL REFERENCES t_core_workflow_template(template_uuid),
    step_order      INTEGER NOT NULL,
    step_name       VARCHAR(100) NOT NULL,
    step_type       VARCHAR(20) NOT NULL DEFAULT 'SEQUENTIAL',
    approver_type   VARCHAR(20) NOT NULL,
    approver_uuid   UUID,
    approver_role   VARCHAR(50),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    UNIQUE (template_uuid, step_order)
);

-- 결재
CREATE TABLE t_core_approval (
    approval_uuid   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    template_uuid   UUID REFERENCES t_core_workflow_template(template_uuid),
    requester_uuid  UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    title           VARCHAR(500) NOT NULL,
    description     TEXT,
    current_step    INTEGER NOT NULL DEFAULT 1,
    total_steps     INTEGER NOT NULL,
    completed_at    TIMESTAMP WITH TIME ZONE,
    cancelled_at    TIMESTAMP WITH TIME ZONE,
    version_seq     BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE INDEX idx_approval_document ON t_core_approval(document_uuid);
CREATE INDEX idx_approval_requester ON t_core_approval(requester_uuid);
CREATE INDEX idx_approval_status ON t_core_approval(status);

-- 결재 단계
CREATE TABLE t_core_approval_step (
    step_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    approval_uuid   UUID NOT NULL REFERENCES t_core_approval(approval_uuid),
    step_order      INTEGER NOT NULL,
    step_name       VARCHAR(100) NOT NULL,
    step_type       VARCHAR(20) NOT NULL DEFAULT 'SEQUENTIAL',
    approver_uuid   UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    delegate_uuid   UUID REFERENCES t_auth_user(user_uuid),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    comment         TEXT,
    decided_at      TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (approval_uuid, step_order)
);

CREATE INDEX idx_approval_step_approver ON t_core_approval_step(approver_uuid, status);

-- 결재 이력
CREATE TABLE t_core_approval_history (
    history_uuid    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    approval_uuid   UUID NOT NULL REFERENCES t_core_approval(approval_uuid),
    step_order      INTEGER,
    action          VARCHAR(20) NOT NULL,
    actor_uuid      UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    comment         TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- 대리결재
CREATE TABLE t_core_approval_delegation (
    delegation_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    delegator_uuid  UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    delegate_uuid   UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    reason          VARCHAR(500),
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

-- ==================== 공유/접근요청 ====================

-- 공유 링크
CREATE TABLE t_core_share_link (
    link_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    link_token      VARCHAR(100) NOT NULL UNIQUE,
    permission_level VARCHAR(20) NOT NULL DEFAULT 'READ',
    password_hash   VARCHAR(200),
    require_login   BOOLEAN NOT NULL DEFAULT FALSE,
    max_access_count INTEGER,
    access_count    INTEGER NOT NULL DEFAULT 0,
    expires_at      TIMESTAMP WITH TIME ZONE,
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE INDEX idx_share_link_token ON t_core_share_link(link_token);

-- 사용자 직접 공유
CREATE TABLE t_core_document_share (
    share_uuid      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    shared_with_uuid UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    permission_level VARCHAR(20) NOT NULL DEFAULT 'READ',
    message         VARCHAR(500),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    UNIQUE (document_uuid, shared_with_uuid)
);

-- 접근 요청
CREATE TABLE t_core_access_request (
    request_uuid    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_type     VARCHAR(20) NOT NULL,
    target_uuid     UUID NOT NULL,
    requester_uuid  UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    owner_uuid      UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    request_type    VARCHAR(20) NOT NULL,
    reason          TEXT NOT NULL,
    request_days    INTEGER NOT NULL DEFAULT 30,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    decided_at      TIMESTAMP WITH TIME ZONE,
    decided_by_uuid UUID,
    reject_reason   VARCHAR(500),
    granted_until   TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE INDEX idx_access_request_owner ON t_core_access_request(owner_uuid, status);
CREATE INDEX idx_access_request_requester ON t_core_access_request(requester_uuid);

-- ==================== 템플릿 ====================

CREATE TABLE t_core_template (
    template_uuid   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_name   VARCHAR(200) NOT NULL,
    description     VARCHAR(500),
    category        VARCHAR(50) NOT NULL,
    file_type       VARCHAR(20) NOT NULL DEFAULT 'DOCX',
    storage_key     VARCHAR(1000),
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    current_version INTEGER NOT NULL DEFAULT 1,
    version_seq     BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE TABLE t_core_template_field (
    field_uuid      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_uuid   UUID NOT NULL REFERENCES t_core_template(template_uuid),
    field_name      VARCHAR(100) NOT NULL,
    field_label     VARCHAR(200) NOT NULL,
    field_type      VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    required_flag   BOOLEAN NOT NULL DEFAULT FALSE,
    default_value   VARCHAR(500),
    placeholder     VARCHAR(200),
    options         JSONB,
    sort_order      INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID
);

CREATE TABLE t_core_template_version (
    version_uuid    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_uuid   UUID NOT NULL REFERENCES t_core_template(template_uuid),
    version_number  INTEGER NOT NULL,
    storage_key     VARCHAR(1000) NOT NULL,
    change_summary  VARCHAR(500),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    UNIQUE (template_uuid, version_number)
);

-- ==================== 보존정책 ====================

CREATE TABLE t_core_retention_policy (
    policy_uuid     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    policy_name     VARCHAR(200) NOT NULL,
    document_type   VARCHAR(50) NOT NULL UNIQUE,
    retention_years INTEGER NOT NULL,
    description     VARCHAR(500),
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE TABLE t_core_retention_schedule (
    schedule_uuid   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    policy_uuid     UUID REFERENCES t_core_retention_policy(policy_uuid),
    original_expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    current_expires_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    extension_count INTEGER NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE INDEX idx_retention_schedule_expires ON t_core_retention_schedule(current_expires_at) WHERE status = 'ACTIVE';
