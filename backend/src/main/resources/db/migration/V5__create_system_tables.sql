-- 시스템, 배치, 알림, 통계 테이블
SET search_path TO edms;

-- ==================== 시스템 (t_sys_) ====================

-- API 감사 로그
CREATE TABLE t_sys_api_audit_log (
    audit_uuid      UUID NOT NULL DEFAULT gen_random_uuid(),
    user_uuid       UUID,
    trace_id        VARCHAR(50),
    http_method     VARCHAR(10) NOT NULL,
    request_uri     VARCHAR(2000) NOT NULL,
    request_body    TEXT,
    response_status INTEGER,
    response_body   TEXT,
    ip_address      VARCHAR(45),
    user_agent      VARCHAR(500),
    elapsed_ms      BIGINT,
    hash_value      VARCHAR(128),
    prev_hash       VARCHAR(128),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    PRIMARY KEY (audit_uuid, created_at)
) PARTITION BY RANGE (created_at);

-- 문서 감사 로그
CREATE TABLE t_sys_document_audit_log (
    audit_uuid      UUID NOT NULL DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL,
    user_uuid       UUID,
    action          VARCHAR(30) NOT NULL,
    detail          JSONB,
    ip_address      VARCHAR(45),
    hash_value      VARCHAR(128),
    prev_hash       VARCHAR(128),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    PRIMARY KEY (audit_uuid, created_at)
) PARTITION BY RANGE (created_at);

-- 2026년 파티션 (월별)
CREATE TABLE t_sys_api_audit_log_2026_01 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
CREATE TABLE t_sys_api_audit_log_2026_02 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-02-01') TO ('2026-03-01');
CREATE TABLE t_sys_api_audit_log_2026_03 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-03-01') TO ('2026-04-01');
CREATE TABLE t_sys_api_audit_log_2026_04 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-04-01') TO ('2026-05-01');
CREATE TABLE t_sys_api_audit_log_2026_05 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-05-01') TO ('2026-06-01');
CREATE TABLE t_sys_api_audit_log_2026_06 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
CREATE TABLE t_sys_api_audit_log_2026_07 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');
CREATE TABLE t_sys_api_audit_log_2026_08 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');
CREATE TABLE t_sys_api_audit_log_2026_09 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');
CREATE TABLE t_sys_api_audit_log_2026_10 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-10-01') TO ('2026-11-01');
CREATE TABLE t_sys_api_audit_log_2026_11 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-11-01') TO ('2026-12-01');
CREATE TABLE t_sys_api_audit_log_2026_12 PARTITION OF t_sys_api_audit_log FOR VALUES FROM ('2026-12-01') TO ('2027-01-01');

CREATE TABLE t_sys_doc_audit_log_2026_01 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
CREATE TABLE t_sys_doc_audit_log_2026_02 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-02-01') TO ('2026-03-01');
CREATE TABLE t_sys_doc_audit_log_2026_03 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-03-01') TO ('2026-04-01');
CREATE TABLE t_sys_doc_audit_log_2026_04 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-04-01') TO ('2026-05-01');
CREATE TABLE t_sys_doc_audit_log_2026_05 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-05-01') TO ('2026-06-01');
CREATE TABLE t_sys_doc_audit_log_2026_06 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
CREATE TABLE t_sys_doc_audit_log_2026_07 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');
CREATE TABLE t_sys_doc_audit_log_2026_08 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');
CREATE TABLE t_sys_doc_audit_log_2026_09 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');
CREATE TABLE t_sys_doc_audit_log_2026_10 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-10-01') TO ('2026-11-01');
CREATE TABLE t_sys_doc_audit_log_2026_11 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-11-01') TO ('2026-12-01');
CREATE TABLE t_sys_doc_audit_log_2026_12 PARTITION OF t_sys_document_audit_log FOR VALUES FROM ('2026-12-01') TO ('2027-01-01');

-- 시스템 설정
CREATE TABLE t_sys_config (
    config_uuid     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key      VARCHAR(200) NOT NULL UNIQUE,
    config_value    TEXT NOT NULL,
    description     VARCHAR(500),
    category        VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

-- ==================== 배치 (t_batch_) ====================

CREATE TABLE t_batch_job (
    job_uuid        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_code        VARCHAR(50) NOT NULL UNIQUE,
    job_name        VARCHAR(200) NOT NULL,
    description     VARCHAR(500),
    job_class       VARCHAR(300) NOT NULL,
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE TABLE t_batch_schedule (
    schedule_uuid   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_uuid        UUID NOT NULL REFERENCES t_batch_job(job_uuid),
    cron_expression VARCHAR(100) NOT NULL,
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE TABLE t_batch_run (
    run_uuid        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_uuid        UUID NOT NULL REFERENCES t_batch_job(job_uuid),
    status          VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
    trigger_type    VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    started_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    completed_at    TIMESTAMP WITH TIME ZONE,
    result_summary  JSONB,
    error_message   TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_batch_run_job ON t_batch_run(job_uuid, started_at DESC);

-- ==================== 알림 (t_noti_) ====================

CREATE TABLE t_noti_template (
    template_uuid   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_code   VARCHAR(50) NOT NULL UNIQUE,
    channel_type    VARCHAR(20) NOT NULL,
    subject         VARCHAR(500),
    body            TEXT NOT NULL,
    variables       JSONB,
    active_flag     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by_uuid UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by_uuid UUID
);

CREATE TABLE t_noti_alert (
    alert_uuid      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_uuid       UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    alert_type      VARCHAR(50) NOT NULL,
    title           VARCHAR(500) NOT NULL,
    message         TEXT,
    link            VARCHAR(1000),
    reference_type  VARCHAR(30),
    reference_uuid  UUID,
    read_flag       BOOLEAN NOT NULL DEFAULT FALSE,
    read_at         TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_alert_user_unread ON t_noti_alert(user_uuid, read_flag, created_at DESC);

CREATE TABLE t_noti_alert_log (
    log_uuid        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    alert_uuid      UUID REFERENCES t_noti_alert(alert_uuid),
    channel_type    VARCHAR(20) NOT NULL,
    recipient       VARCHAR(500) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sent_at         TIMESTAMP WITH TIME ZONE,
    error_message   TEXT,
    retry_count     INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE t_noti_user_preference (
    preference_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_uuid       UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    alert_type      VARCHAR(50) NOT NULL,
    email_flag      BOOLEAN NOT NULL DEFAULT TRUE,
    teams_flag      BOOLEAN NOT NULL DEFAULT FALSE,
    kakao_flag      BOOLEAN NOT NULL DEFAULT FALSE,
    sms_flag        BOOLEAN NOT NULL DEFAULT FALSE,
    push_flag       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE,
    UNIQUE (user_uuid, alert_type)
);

-- ==================== 통계 (t_stat_) ====================

CREATE TABLE t_stat_document_daily (
    stat_uuid           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stat_date           DATE NOT NULL,
    total_documents     BIGINT NOT NULL DEFAULT 0,
    new_documents       BIGINT NOT NULL DEFAULT 0,
    total_storage_bytes BIGINT NOT NULL DEFAULT 0,
    active_users        INTEGER NOT NULL DEFAULT 0,
    downloads           BIGINT NOT NULL DEFAULT 0,
    uploads             BIGINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (stat_date)
);

CREATE TABLE t_stat_ai_usage (
    stat_uuid       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stat_date       DATE NOT NULL,
    task_type       VARCHAR(30) NOT NULL,
    total_requests  BIGINT NOT NULL DEFAULT 0,
    success_count   BIGINT NOT NULL DEFAULT 0,
    failure_count   BIGINT NOT NULL DEFAULT 0,
    total_tokens    BIGINT NOT NULL DEFAULT 0,
    total_cost      NUMERIC(10,4) NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (stat_date, task_type)
);
