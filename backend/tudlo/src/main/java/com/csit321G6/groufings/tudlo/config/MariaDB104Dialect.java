package com.csit321G6.groufings.tudlo.config;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

/**
 * Custom Hibernate dialect for MariaDB 10.4.x compatibility.
 * Hibernate 7's default MariaDBDialect assumes >= 10.6 and unconditionally emits
 * {@code ALTER TABLE IF EXISTS}, which was introduced in MariaDB 10.5.2.
 */
public class MariaDB104Dialect extends MariaDBDialect {

    public MariaDB104Dialect() {
        super(DatabaseVersion.make(10, 4));
    }

    public MariaDB104Dialect(DialectResolutionInfo info) {
        super(info);
    }

    @Override
    protected DatabaseVersion getMinimumSupportedVersion() {
        return DatabaseVersion.make(10, 4);
    }

    @Override
    public boolean supportsIfExistsAfterAlterTable() {
        return getVersion().isSameOrAfter(10, 5, 2);
    }
}
