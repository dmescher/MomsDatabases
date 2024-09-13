package org.golfballdm.shared;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Query {
    @Getter
    private String preparedStatementString;

    private List<String> tableNames;
    private int paramCount;
    private Map<String, String> parameters;
    private Map<String, FieldTypes> parameterTypes;
    private List<Field> allModelFields;
    private List<String> allFieldNames;
    public static final String distinctFieldParameterName = "distinct_field";
    public static final String sortFieldParameterName = "sort_field";
    public static final String sortDirection = "sort_direction";
    private static final int COLUMN_NAME_DEFAULT_SIZE = 15;

    private enum FieldTypes {
        STRING,
        INTEGER,
        FLOAT
    }

    private Query() {
    }

    public Query(Map<String, String> parameters, String tableName, Object t) {
        this.parameters = parameters;
        this.paramCount = parameters.size();
        this.parameterTypes = new HashMap<>();
        this.allModelFields = List.of(t.getClass().getDeclaredFields());
        this.tableNames = List.of(tableName);
        this.allFieldNames = new ArrayList<>();
        for (Field f : allModelFields) {
            this.allFieldNames.add(f.getName());
        }
        createTypeMap();
    }

    private void createTypeMap() {
        for (String s : parameters.keySet()) {
            String val = parameters.get(s);
            boolean isFloat = val.matches("[+-]?[0-9]*\\.[0-9]+");
            boolean isInt = val.matches("[+-]?[0-9]+");
            if (isInt) {
                parameterTypes.put(s,FieldTypes.INTEGER);
            } else if (isFloat) {
                parameterTypes.put(s,FieldTypes.FLOAT);
            } else {
                parameterTypes.put(s,FieldTypes.STRING);
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
                case ".GT" -> " > ";
                case ".LT" -> " < ";
                case ".GE" -> " >= ";
                case ".LE" -> " <= ";
                case ".NE" -> " <> ";
                case ".LK" -> " LIKE ";
                default -> "=";
            };

            String columnName = StringUtils.substringBefore(s, ".");
            if (null == columnNamePresent(columnName)) {
                throw new SQLException("Bad column name "+s);
            }

            whereClauses.add(columnName+operand+"?");
        }

        // Build the SQL String
        StringBuilder sqlStatement = new StringBuilder(parameterList.size()*COLUMN_NAME_DEFAULT_SIZE);
        sqlStatement.append("SELECT ");

        if (parameters.containsKey(Query.distinctFieldParameterName)) {
            // Append DISTINCT(distinct_field)
            sqlStatement.append("DISTINCT(")
                    .append(parameters.get(Query.distinctFieldParameterName))
                    .append(") FROM ");
        } else {
            sqlStatement.append("* FROM ");
        }

        if (tableNames.size() == 1) {
            sqlStatement.append(tableNames.getFirst()).append(" ");
        } else {
            // TODO:  Implement multiple table names.  Will require some way of specifying join fields, among other things
        }

        sqlStatement.append(" WHERE ");
        for (int count = 0; count<whereClauses.size(); count++) {
            sqlStatement.append(whereClauses.get(count));
            if (count+1 < whereClauses.size()) {
                sqlStatement.append(" AND ");
            }
        }

        if (parameters.containsKey(sortFieldParameterName)) {
            sqlStatement.append(" ORDER BY ").append(parameters.get(sortFieldParameterName)).append(" ");
            if (parameters.containsKey(sortDirection)) {
                sqlStatement.append(" ").append(parameters.get(sortDirection)).append(" ");
            }
        }

        sqlStatement.append(";");
        preparedStatementString = sqlStatement.toString();
        System.out.println(preparedStatementString);

        if (null == conn) {
            return null;
        }

        // Create the prepared statement, with variables filled in
        PreparedStatement ps = conn.prepareStatement(preparedStatementString);

        int index = 1;
        for (String s : parameterList) {
            switch (parameterTypes.get(s)) {
                case FLOAT -> ps.setFloat(index, Float.parseFloat(parameters.get(s)));
                case INTEGER -> ps.setInt(index, Integer.parseInt(parameters.get(s)));
                case STRING -> ps.setString(index, parameters.get(s));
            }
            index++;
        }

        return ps;

    }
}
