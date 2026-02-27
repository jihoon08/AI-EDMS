package com.edms.storage;

import org.springframework.core.io.Resource;

import java.io.InputStream;

public interface StorageService {

    StorageResult upload(String key, InputStream inputStream, long size, String contentType);

    Resource download(String key);

    void delete(String key);

    boolean exists(String key);

    String generateKey(String folder, String filename);
}
