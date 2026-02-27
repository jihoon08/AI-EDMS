package com.edms.storage;

import com.edms.common.exception.BusinessException;
import com.edms.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    @Value("${app.storage.local.base-path:./storage}")
    private String basePath;

    private Path root;

    @PostConstruct
    void init() {
        this.root = Paths.get(basePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
            log.info("Local storage initialized at: {}", root);
        } catch (IOException e) {
            throw new RuntimeException("스토리지 디렉토리 생성 실패: " + root, e);
        }
    }

    @Override
    public StorageResult upload(String key, InputStream inputStream, long size, String contentType) {
        try {
            Path target = root.resolve(key).normalize();
            if (!target.startsWith(root)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "잘못된 저장 경로입니다");
            }
            Files.createDirectories(target.getParent());
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            long actualSize = Files.size(target);
            log.debug("File uploaded: key={}, size={}", key, actualSize);
            return StorageResult.builder()
                    .key(key)
                    .size(actualSize)
                    .contentType(contentType)
                    .build();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.STORAGE_UPLOAD_FAILED, e.getMessage());
        }
    }

    @Override
    public Resource download(String key) {
        Path file = root.resolve(key).normalize();
        if (!file.startsWith(root) || !Files.exists(file)) {
            throw new BusinessException(ErrorCode.STORAGE_DOWNLOAD_FAILED, "파일을 찾을 수 없습니다: " + key);
        }
        return new FileSystemResource(file);
    }

    @Override
    public void delete(String key) {
        try {
            Path file = root.resolve(key).normalize();
            if (file.startsWith(root)) {
                Files.deleteIfExists(file);
                log.debug("File deleted: key={}", key);
            }
        } catch (IOException e) {
            log.warn("File delete failed: key={}", key, e);
        }
    }

    @Override
    public boolean exists(String key) {
        Path file = root.resolve(key).normalize();
        return file.startsWith(root) && Files.exists(file);
    }

    @Override
    public String generateKey(String folder, String filename) {
        LocalDate now = LocalDate.now();
        String ext = "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            ext = filename.substring(dotIndex);
        }
        return String.format("%s/%d/%02d/%02d/%s%s",
                folder, now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                UUID.randomUUID(), ext);
    }
}
