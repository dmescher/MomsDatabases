package org.golfballdm.shared;

import jakarta.ws.rs.core.MultivaluedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterValidatorImpl implements ParameterValidator {
    private static final String distinctFieldParameterName = Query.distinctFieldParameterName;
    private static final String sortFieldParameterName = Query.sortFieldParameterName;
    private static final String sortDirectionName = Query.sortDirection;
    private static final ColumnNameValidator columnNameValidator = new ColumnNameValidatorImpl();
    private static Logger logger = LoggerFactory.getLogger(ParameterValidatorImpl.class);

    @Override
    public Map<String, String> validateParameters(MultivaluedMap<String, String> parameters) {
        // Iterate through keys, make sure only one value per key
        for (String s: parameters.keySet()) {
            List<String> valList = parameters.get(s);
            if (valList.size() > 1) {
                throw new IllegalArgumentException("Duplicate values for parameter "+s);
            }
        }

        HashMap<String, String> rtn = new HashMap<>();

        // Parse field/value pairs in query parameters
        int fieldsProcessed = 1;
        boolean fieldFound;
        boolean rtnField = false;
        boolean sortField = false;
        boolean sortType = false;
        do {
            String keyName = "field"+ (fieldsProcessed < 10 ? "0" : fieldsProcessed / 10) + (fieldsProcessed % 10);
            String valName = "value"+ (fieldsProcessed < 10 ? "0" : fieldsProcessed / 10) + (fieldsProcessed % 10);
            if (parameters.containsKey(keyName)) {
                if (parameters.containsKey(valName)) {
                    fieldFound=true;
                    String keyVal = parameters.getFirst(keyName);
                    String valVal = parameters.getFirst(valName);
                    rtn.put(keyVal, valVal);
                    fieldsProcessed++;
                } else {
                    throw new IllegalArgumentException("Missing value for key "+keyName);
                }
            } else {
                fieldFound=false;
                if (parameters.containsKey(valName)) {
                    throw new IllegalArgumentException("Unpaired value "+valName);
                }
            }
        } while (fieldFound);
        fieldsProcessed--;  // Since the index starts at 1, this adjusts it back to the correct value after the loop finishes

        // Check for return_column_name
        if (parameters.containsKey(distinctFieldParameterName)) {
            String val = parameters.getFirst(distinctFieldParameterName);
            rtn.put(distinctFieldParameterName,val);
            rtnField = true;
        }

        // Check for sort_column_name
        if (parameters.containsKey(sortFieldParameterName)) {
            String val = parameters.getFirst(sortFieldParameterName);
            rtn.put(sortFieldParameterName,val);
            sortField = true;
        }

        if (parameters.containsKey(sortDirectionName)) {
            if (!sortField) {
                throw new IllegalArgumentException("Sort direction is missing column name");
            }
            String val = parameters.getFirst(sortDirectionName);
            if (!StringUtils.equalsIgnoreCase(val,"asc") &&
                !StringUtils.equalsIgnoreCase(val,"desc")) {
                throw new IllegalArgumentException("Sort direction must be asc/desc");
            }
            rtn.put(sortDirectionName,val);
            sortType = true;
        }

        int queryParamLength = parameters.keySet().size();
        if (queryParamLength != (fieldsProcessed*2)+(rtnField ? 1 : 0)+(sortField ? 1 : 0)+(sortType ? 1 : 0)) {
            System.out.println("queryParamLength = "+queryParamLength);
            System.out.println("fieldsProcessed = "+fieldsProcessed);
            System.out.println("expected count = "+(fieldsProcessed*2+(rtnField ? 1 : 0)+(sortField ? 1 : 0)+(sortType ? 1 : 0)));
            throw new IllegalArgumentException("Invalid field count");
        }

        for (String s: rtn.keySet()) {
            if (!columnNameValidator.validateColumnName(s)) {
                throw new IllegalArgumentException("Invalid column name "+s);
            }
        }

        return rtn;
    }

}
