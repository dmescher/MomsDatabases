package org.golfballdm.shared;

import java.util.Map;

public class Query {
    private String preparedStatement;
    private int paramCount;
    private Map<String, String> parameters;
    private Map<String, String> parameterTypes;
    public static final String distinctFieldParameterName = "distinct_field";

    private Query() {
    }

    public Query(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
