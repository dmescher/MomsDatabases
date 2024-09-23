package org.golfballdm.shared;

public interface ColumnNameValidator {
    /*
     Used for sanity checking column names to prevent SQL injection attacks.
     Since we can't use bind variables for the column names, we need to make sure
     they conform to a standard.  (In this case, alphanumeric characters, dash, and underscore only.
     I have added dot and splat to permit aliasing and operands.)
     */
    /* This only ensures that the column name is syntactically valid.  It does not check it
       against the column names of the table(s), that is done during the query generation.
     */
    boolean validateColumnName(String columnName);
}
