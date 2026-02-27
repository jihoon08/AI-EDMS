# AI-EDMS 프로젝트 가이드

## 프로젝트 개요
AI 기반 전자문서관리시스템 (Electronic Document Management System)

## Tech Stack
- **Backend**: Spring Boot 3.2.x, Java 17, Gradle Kotlin DSL
- **Frontend**: Next.js 14 (App Router), TypeScript, TailwindCSS, Shadcn/ui
- **Database**: PostgreSQL 16 + pgvector, Flyway
- **Cache**: Caffeine 4-tier
- **Storage**: S3 (prod) / Local filesystem (dev)

## 프로젝트 구조
```
AI-EDMS/
├── backend/          # Spring Boot 백엔드
│   └── src/main/java/com/edms/
│       ├── common/   # 공통 인프라
│       ├── document/ # 문서 도메인
│       ├── folder/   # 폴더 도메인
│       ├── workflow/  # 결재 도메인
│       ├── ai/       # AI 파이프라인
│       └── ...
├── frontend/         # Next.js 프론트엔드
└── deploy/           # Docker/배포 설정
```

## 빌드/실행 명령어

### Backend
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
cd backend && ./gradlew compileJava          # 컴파일 확인
cd backend && ./gradlew bootRunLocal         # 로컬 실행
cd backend && ./gradlew test                 # 테스트
```

### Frontend
```bash
cd frontend && npm install                    # 의존성 설치
cd frontend && npm run dev                    # 개발 서버
cd frontend && npm run build                  # 빌드
```

### Database (Docker)
```bash
docker-compose -f deploy/dev/docker-compose.yml up -d  # DB 실행
```

## 아키텍처 규칙
- UUID PK + gen_random_uuid()
- 4-column 감사: created_at, created_by_uuid, updated_at, updated_by_uuid
- 소프트 삭제: deleted_flag, deleted_at
- 낙관적 잠금: @Version version_seq
- API 응답: CommonApiResponse<T> 래퍼
- 에러: ErrorCode + BusinessException
- DB 테이블 접두사: t_auth_, t_core_, t_sys_, t_batch_, t_noti_, t_stat_

## 커밋 규칙
- 한국어 커밋 메시지
- 접두사: feat:, fix:, refactor:, docs:, chore:
