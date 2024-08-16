package org.golfballdm.shared;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

public class Query {
    private String preparedStatementString;
    private int paramCount;
    private Map<String, String> parameters;
    private Map<String, String> parameterTypes;
    public static final String distinctFieldParameterName = "distinct_field";
    public static final String sortFieldParameterName = "sort_field";
    public static final String sortDirection = "sort_direction";

    private Query() {
    }

    public Query(Map<String, String> parameters) {
        this.parameters = parameters;
        this.paramCount = parameters.size();
    }

    public void createTypeMap() {
        for (String s : parameters.keySet()) {
            String val = parameters.get(s);
            boolean isFloat = val.matches("[+-]?[0-9]*\\.[0-9]+");
            boolean isInt = val.matches("[+-]?[0-9]+");
            if (isInt) {
                parameterTypes.put(s,"Integer");
            } else if (isFloat) {
                parameterTypes.put(s,"Float");
            } else {
                parameterTypes.put(s,"String");
            }
        }
    }



    public PreparedStatement generatePreparedStatement(Connection conn) {
        return null;

    }
}
