package org.golfballdm.DAO;

import com.zaxxer.hikari.HikariConfig;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public abstract class GenericDAO {
    protected ConnectionPool connectionPool;
    private HikariConfig poolConfig;
    private boolean configured = false;
    private final String daoName;

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

        HikariConfig poolConfig = createConfiguration(daoName);
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
            System.out.println(properties.getProperty(daoName+".jdbc.url"));
            rtn.setUsername(properties.getProperty(daoName+".jdbc.user"));
            System.out.println(properties.getProperty(daoName+".jdbc.user"));


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failure to load properties for "+configName);
            System.exit(1);
        }

        return rtn;

    }

    public Connection getConnectionFromPool() {
        return null;
    }

    public boolean testConnection() throws IllegalStateException, SQLException {
        if (!configured) {
            throw new IllegalStateException("DAO "+daoName+" not configured.");
        }
        return false;

    }
}
