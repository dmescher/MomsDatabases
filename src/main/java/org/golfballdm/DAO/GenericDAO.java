package org.golfballdm.DAO;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public abstract class GenericDAO {
    protected ConnectionPool connectionPool;
    private HikariConfig poolConfig;
    @Getter private boolean configured = false;
    private final String daoName;
    private String TEST_SQL = "SELECT * from dbo.Empty";
    private DataSource ds = null;
    @Getter private String[] expressQueries = null;

    protected GenericDAO(String daoName) {
        this.daoName = daoName;
    }
    public GenericDAO(String daoName, DataSource ds) {
        this.daoName = daoName;
        this.ds = ds;
    }

    protected synchronized void configure() {
        if (configured) {
            return;
        }

        if (null != ds) {
            configured = true;
            return;
        }

        poolConfig = createConfiguration(daoName);
        connectionPool = new ConnectionPool(poolConfig);
        ds = connectionPool.getDataSource();
        expressQueries = loadExpressQueries();
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
            if (null == properties.getProperty(daoName+".testquery")) {
                rtn.setConnectionTestQuery(TEST_SQL);
            } else {
                rtn.setConnectionTestQuery(properties.getProperty(daoName+".testquery"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failure to load properties for "+configName);
            System.exit(1);
        }

        return rtn;
    }

    private String[] loadExpressQueries() {
        Properties properties = new Properties();
        ArrayList<String> queries = new ArrayList<>();

        try (InputStream input = GenericDAO.class.getClassLoader().getResourceAsStream((daoName+".properties"))) {
            if (null == input) {
                System.out.println(daoName+":  Resource does not exist at path");
                return null;
            }
            properties.load(input);
            int count = 1;
            boolean done = false;
            do {
                String queryVal = properties.getProperty(daoName+".expressquery."+count++);
                if (null == queryVal) {
                    done = true;
                } else {
                    queries.add(queryVal);
                }
            } while (!done);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failure to load properties for "+daoName);
            System.exit(1);
        }

        String[] rtnval = new String[queries.size()];
        if (queries.size() > 1) {
            return queries.toArray(rtnval);
        }
        return null;
    }

    public Connection getConnectionFromPool() throws IllegalStateException, SQLException {
        if (!configured) {
            throw new IllegalStateException("DAO "+daoName+" not configured.");
        }

        return ds.getConnection();
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

    public ResultSet executePS(PreparedStatement ps) throws SQLException {
        return ps.executeQuery();
    }


}
