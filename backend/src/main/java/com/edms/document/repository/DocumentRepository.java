package com.edms.document.repository;

import com.edms.document.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    @Query("SELECT d FROM Document d WHERE d.deletedFlag = false ORDER BY d.createdAt DESC")
    Page<Document> findAllActive(Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.deletedFlag = false AND d.folderUuid = :folderUuid")
    Page<Document> findByFolderUuid(@Param("folderUuid") UUID folderUuid, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.documentUuid = :uuid AND d.deletedFlag = false")
    Optional<Document> findActiveById(@Param("uuid") UUID uuid);

    @Query("SELECT d FROM Document d WHERE d.deletedFlag = false " +
           "AND (:documentType IS NULL OR d.documentType = :documentType) " +
           "AND (:status IS NULL OR d.status = :status) " +
           "ORDER BY d.createdAt DESC")
    Page<Document> searchWithoutKeyword(@Param("documentType") String documentType,
                                        @Param("status") String status,
                                        Pageable pageable);

    @Query(value = "SELECT d.* FROM edms.t_core_document d WHERE d.deleted_flag = false " +
           "AND (:documentType IS NULL OR d.document_type = CAST(:documentType AS VARCHAR)) " +
           "AND (:status IS NULL OR d.status = CAST(:status AS VARCHAR)) " +
           "AND LOWER(d.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS VARCHAR), '%')) " +
           "ORDER BY d.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM edms.t_core_document d WHERE d.deleted_flag = false " +
           "AND (:documentType IS NULL OR d.document_type = CAST(:documentType AS VARCHAR)) " +
           "AND (:status IS NULL OR d.status = CAST(:status AS VARCHAR)) " +
           "AND LOWER(d.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS VARCHAR), '%'))",
           nativeQuery = true)
    Page<Document> searchWithKeyword(@Param("keyword") String keyword,
                                     @Param("documentType") String documentType,
                                     @Param("status") String status,
                                     Pageable pageable);

    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(document_number FROM '[0-9]+$') AS INTEGER)), 0) " +
                   "FROM edms.t_core_document WHERE document_number LIKE :prefix || '%'",
           nativeQuery = true)
    int findMaxSequence(@Param("prefix") String prefix);
}
