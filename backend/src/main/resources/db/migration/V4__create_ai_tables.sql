-- AI 관련 테이블
SET search_path TO edms;

-- AI 분류 결과
CREATE TABLE t_core_ai_classification (
    classification_uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid       UUID NOT NULL REFERENCES t_core_document(document_uuid),
    predicted_type      VARCHAR(50) NOT NULL,
    confidence          DOUBLE PRECISION NOT NULL,
    model_version       VARCHAR(50),
    verified_flag       BOOLEAN NOT NULL DEFAULT FALSE,
    verified_by_uuid    UUID,
    verified_at         TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ai_classification_doc ON t_core_ai_classification(document_uuid);

-- AI 요약
CREATE TABLE t_core_ai_summary (
    summary_uuid    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    summary_type    VARCHAR(20) NOT NULL,
    content         TEXT NOT NULL,
    model_version   VARCHAR(50),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ai_summary_doc ON t_core_ai_summary(document_uuid);

-- OCR 결과
CREATE TABLE t_core_ai_ocr_result (
    ocr_uuid        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    extracted_text  TEXT NOT NULL,
    page_number     INTEGER,
    confidence      DOUBLE PRECISION,
    engine          VARCHAR(30) NOT NULL DEFAULT 'TESSERACT',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ai_ocr_doc ON t_core_ai_ocr_result(document_uuid);

-- AI 임베딩 (pgvector)
CREATE TABLE t_core_ai_embedding (
    embedding_uuid  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    chunk_index     INTEGER NOT NULL DEFAULT 0,
    chunk_text      TEXT NOT NULL,
    embedding       vector(1536) NOT NULL,
    model_version   VARCHAR(50),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ai_embedding_doc ON t_core_ai_embedding(document_uuid);
CREATE INDEX idx_ai_embedding_vector ON t_core_ai_embedding USING hnsw (embedding vector_cosine_ops);

-- AI Q&A 이력
CREATE TABLE t_core_ai_qa_history (
    qa_uuid         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_uuid       UUID NOT NULL REFERENCES t_auth_user(user_uuid),
    question        TEXT NOT NULL,
    answer          TEXT NOT NULL,
    source_documents JSONB,
    model_version   VARCHAR(50),
    feedback        VARCHAR(20),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ai_qa_user ON t_core_ai_qa_history(user_uuid, created_at DESC);

-- AI 처리 큐
CREATE TABLE t_core_ai_processing_queue (
    queue_uuid      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_uuid   UUID NOT NULL REFERENCES t_core_document(document_uuid),
    task_type       VARCHAR(30) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority        INTEGER NOT NULL DEFAULT 5,
    retry_count     INTEGER NOT NULL DEFAULT 0,
    max_retries     INTEGER NOT NULL DEFAULT 3,
    error_message   TEXT,
    started_at      TIMESTAMP WITH TIME ZONE,
    completed_at    TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ai_queue_status ON t_core_ai_processing_queue(status, priority DESC, created_at);
CREATE INDEX idx_ai_queue_doc ON t_core_ai_processing_queue(document_uuid);
