package org.golfballdm.shared;

import org.golfballdm.DAO.GenericDAO;
import org.golfballdm.models.FreeResident;
import org.golfballdm.orm.GenericMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class QueryExecutor {
    private GenericDAO dao;
    private String[] tables;
    private Query query;
    private GenericMapper mapper;
    private Map<String, String> parameters;

    public QueryExecutor(GenericDAO dao, Map<String, String> parameters, String[] tables, GenericMapper mapper) {
        this.dao = dao;
        this.parameters = parameters;
        this.tables = Arrays.copyOf(tables, tables.length);
        this.mapper = mapper;
    }

    public void buildQuery() throws SQLException {
        query = new Query(this.parameters, tables, new FreeResident());
    }

    public List<Object> executeQuery() throws SQLException {
        try (Connection conn = dao.getConnectionFromPool();
             PreparedStatement ps = query.generatePreparedStatement(conn);
             ResultSet rs = dao.executePS(ps)) {
            return mapper.map(rs);
        }
    }
}
