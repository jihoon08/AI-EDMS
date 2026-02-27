package com.edms.storage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StorageResult {
    private final String key;
    private final long size;
    private final String contentType;
}
