package com.edms.folder.repository;

import com.edms.folder.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

    @Query("SELECT f FROM Folder f WHERE f.deletedFlag = false ORDER BY f.materializedPath, f.sortOrder")
    List<Folder> findAllActive();

    @Query("SELECT f FROM Folder f WHERE f.parentUuid = :parentUuid AND f.deletedFlag = false ORDER BY f.sortOrder")
    List<Folder> findByParentUuid(@Param("parentUuid") UUID parentUuid);

    @Query("SELECT f FROM Folder f WHERE f.parentUuid IS NULL AND f.deletedFlag = false ORDER BY f.sortOrder")
    List<Folder> findRootFolders();

    @Query("SELECT f FROM Folder f WHERE f.folderUuid = :uuid AND f.deletedFlag = false")
    Optional<Folder> findActiveById(@Param("uuid") UUID uuid);

    @Query("SELECT f FROM Folder f WHERE f.materializedPath LIKE :pathPrefix || '%' AND f.deletedFlag = false")
    List<Folder> findDescendants(@Param("pathPrefix") String pathPrefix);

    @Query("SELECT COUNT(f) FROM Folder f WHERE f.parentUuid = :parentUuid AND f.deletedFlag = false")
    long countChildren(@Param("parentUuid") UUID parentUuid);
}
