package org.golfballdm.shared;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Query<T> {
    private String preparedStatementString;
    private int paramCount;
    private Map<String, String> parameters;
    private Map<String, String> parameterTypes;
    private List<Field> allModelFields;
    private List<String> allFieldNames;
    public static final String distinctFieldParameterName = "distinct_field";
    public static final String sortFieldParameterName = "sort_field";
    public static final String sortDirection = "sort_direction";

    private Query() {
    }

    public Query(Map<String, String> parameters, Class<T> t) {
        this.parameters = parameters;
        this.paramCount = parameters.size();
        this.parameterTypes = new HashMap<>();
        this.allModelFields = List.of(t.getFields());
        this.allFieldNames = new ArrayList<>();
        for (Field f : allModelFields) {
            this.allFieldNames.add(f.getName());
        }
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

    private String columnNamePresent(String s) {
        for (String fieldNameCursor : this.allFieldNames) {
            if (StringUtils.equalsIgnoreCase(fieldNameCursor, s)) {
                return fieldNameCursor;
            }
        }
        return null;
    }

    public PreparedStatement generatePreparedStatement(Connection conn) throws SQLException {
        List<String> whereClauses = new ArrayList<>();

        ArrayList<String> parameterList = new ArrayList<>(parameters.keySet());
        Collections.sort(parameterList);

        for (String s : parameterList) {
            if (StringUtils.equalsIgnoreCase(s, distinctFieldParameterName) ||
                StringUtils.equalsIgnoreCase(s, sortFieldParameterName) ||
                StringUtils.equalsIgnoreCase(s, sortDirection)) {
                continue;
            }

            String operand = switch(StringUtils.right(s, 3)) {
                case ".GT" -> ">";
                case ".LT" -> "<";
                case ".GE" -> ">=";
                case ".LE" -> "<=";
                case ".NE" -> "<>";
                case ".LK" -> "LIKE";
                default -> "=";
            };

            String columnName = StringUtils.substringBefore(s, ".");
            if (null == columnNamePresent(columnName)) {
                throw new SQLException("Bad column name "+s);
            }

            whereClauses.add(columnName+operand+"?");
        }

        // Build the SQL String
        // Create the prepared statement

        return null;

    }
}
