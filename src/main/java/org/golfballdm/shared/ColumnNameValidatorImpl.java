package org.golfballdm.shared;

public class ColumnNameValidatorImpl implements ColumnNameValidator {
    @Override
    public boolean validateColumnName(String columnName) {
        for (char c : columnName.toCharArray()) {
            if (!(c >= 'a' && c <= 'z') &&
                    !(c >= 'A' && c <= 'Z') &&
                    !(c >= '0' && c <= '9') &&
                    !(c == '_') &&
                    !(c == '*') &&
                    !(c == '-')) {
                return false;
            }
        }
        return true;

    }
}
