package org.golfballdm.DAO;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private final HikariConfig config;
    private final HikariDataSource conPool;

    public ConnectionPool(HikariConfig config) {
        this.config = config;
        this.conPool = new HikariDataSource(this.config);
    }

    public Connection getConnection() throws SQLException {
        return conPool.getConnection();
    }

    public DataSource getDataSource() {
        return conPool;
    }
}
