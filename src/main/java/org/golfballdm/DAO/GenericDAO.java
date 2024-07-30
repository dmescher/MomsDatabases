package org.golfballdm.DAO;

import com.zaxxer.hikari.HikariConfig;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public abstract class GenericDAO {
    protected ConnectionPool connectionPool;
    private HikariConfig poolConfig;
    private boolean configured = false;
    private final String daoName;
    private static final String TEST_SQL = "SELECT * from dbo.Empty";

    protected GenericDAO(String daoName) {
        this.daoName = daoName;
    }

    public boolean isConfigured() {
        return configured;
    }

    protected synchronized void configure() {
        if (configured) {
            return;
        }

        poolConfig = createConfiguration(daoName);
        connectionPool = new ConnectionPool(poolConfig);
        configured = true;
    }

    // Creates the (Hikari) pool configuration
    private HikariConfig createConfiguration(String configName) {
        Properties properties = new Properties();
        HikariConfig rtn = new HikariConfig();

        try (InputStream input = GenericDAO.class.getClassLoader().getResourceAsStream(configName+".properties")) {
            if (null == input) {
                System.out.println(daoName+":  Resource does not exist at path");
                return null;
            }
            properties.load(input);
            rtn.setJdbcUrl(properties.getProperty(daoName+".jdbc.url"));
            rtn.setDriverClassName(properties.getProperty(daoName+".jdbc.driver"));
            rtn.setUsername(properties.getProperty(daoName+".jdbc.user"));
            rtn.setPassword(properties.getProperty(daoName+".jdbc.password"));
            rtn.setMaximumPoolSize(Integer.parseInt(properties.getProperty(daoName+".hikari.maxpoolsize")));
            rtn.setConnectionTestQuery(TEST_SQL);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failure to load properties for "+configName);
            System.exit(1);
        }

        return rtn;
    }

    private Connection getConnectionFromPool() throws IllegalStateException, SQLException {
        if (!configured) {
            throw new IllegalStateException("DAO "+daoName+" not configured.");
        }

        return connectionPool.getConnection();
    }

    public boolean testConnection() throws IllegalStateException, SQLException {
        if (!configured) {
            throw new IllegalStateException("DAO "+daoName+" not configured.");
        }

        try (Connection connection = getConnectionFromPool();
             PreparedStatement ps = connection.prepareStatement(TEST_SQL)) {
            ps.execute();
            // We don't care about the contents of the result set (which should be empty anyway)
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
