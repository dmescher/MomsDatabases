package org.golfballdm.DAO;

import com.zaxxer.hikari.HikariConfig;

public class ConnectionPool {
    private final HikariConfig config;

    public ConnectionPool(HikariConfig config) {
        this.config = config;
    }
}
