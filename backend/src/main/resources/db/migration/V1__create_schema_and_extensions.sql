-- AI-EDMS: 스키마 생성 및 확장 설치
CREATE SCHEMA IF NOT EXISTS edms;
SET search_path TO edms;

-- pgvector 확장
CREATE EXTENSION IF NOT EXISTS vector;

-- uuid 생성 함수
CREATE EXTENSION IF NOT EXISTS pgcrypto;
