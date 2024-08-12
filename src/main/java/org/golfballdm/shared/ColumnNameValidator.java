package org.golfballdm.shared;

public interface ColumnNameValidator {
    /*
     Used for sanity checking column names to prevent SQL injection attacks.
     Since we can't use bind variables for the column names, we need to make sure
     they conform to a standard.  (In this case, alphanumeric characters, dash, and underscore only)
     */
    boolean validateColumnName(String columnName);
}
