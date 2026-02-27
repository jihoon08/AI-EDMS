package com.edms.common.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<Boolean> FORCE_WRITE = ThreadLocal.withInitial(() -> false);

    public enum DataSourceType {
        WRITE, READ
    }

    @Override
    protected Object determineCurrentLookupKey() {
        if (Boolean.TRUE.equals(FORCE_WRITE.get())) {
            log.debug("Routing to WRITE (forced)");
            return DataSourceType.WRITE;
        }

        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            log.debug("Routing to WRITE (no active transaction)");
            return DataSourceType.WRITE;
        }

        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        DataSourceType type = readOnly ? DataSourceType.READ : DataSourceType.WRITE;
        log.debug("Routing to {} (readOnly={})", type, readOnly);
        return type;
    }

    public static void setForceWrite(boolean force) {
        FORCE_WRITE.set(force);
    }

    public static void clearForceWrite() {
        FORCE_WRITE.remove();
    }
}
