package org.golfballdm.shared;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Stream;
import java.sql.SQLException;

/*
 Field name format:
   <field name> ::= <table alias> <column name> <operand>
   <table alias> ::= <alias> "*" | ""
   <operand> ::= "" | ".GT" | ".LT" | ".GE" | ".LE" | ".NE" | ".LK"
 */

public class Query {
    private List<String> tableNames;

    @Getter private String preparedStatementString;

    private Map<String, String> parameters;
    private Map<String, Pair<FieldTypes,Object>> preparedStatementParameters = new HashMap<>();

    private List<String> allFieldNames = new ArrayList<>();

    public static final String distinctFieldParameterName = "distinct_field";
    public static final String sortFieldParameterName = "sort_field";
    public static final String sortDirection = "sort_direction";
    private static final int COLUMN_NAME_DEFAULT_SIZE = 15;

    private Query() {
    }

    public Query(Map<String, String> parameters, String[] tablenames, Object t) {
        this.parameters = parameters;
        createParameterMap();

        allFieldNames = Stream.of(t.getClass().getDeclaredFields()).map(Field::getName).toList();

        this.tableNames = List.of(tablenames);
    }

    public void createParameterMap() {
        for (String s : parameters.keySet()) {
            String val = parameters.get(s);
            boolean isFloat = val.matches("[+-]?[0-9]*\\.[0-9]+");
            boolean isInt = val.matches("[+-]?[0-9]+");
            FieldTypes fieldType;
            Object fieldValue;
            if (isInt) {
                fieldType = FieldTypes.INTEGER;
                fieldValue = Integer.valueOf(val);
            } else if (isFloat) {
                fieldType = FieldTypes.FLOAT;
                fieldValue = Float.valueOf(val);
            } else {
                fieldType = FieldTypes.STRING;
                fieldValue = val;
            }

            Pair<FieldTypes, Object> tuple = new ImmutablePair<>(fieldType, fieldValue);
            preparedStatementParameters.put(s, tuple);
        }
    }

    private String columnNamePresent(String s) {
        // We don't use List.contains, because we want to ignore the case.
        for (String fieldNameCursor : this.allFieldNames) {
            if (StringUtils.equalsIgnoreCase(fieldNameCursor, s)) {
                return fieldNameCursor;
            }
        }
        return null;
    }



    public String generatePreparedStatementString() throws SQLException {
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

        return preparedStatementString;
    }


    public PreparedStatement generatePreparedStatement(Connection conn) {
        return null;

    }
}
