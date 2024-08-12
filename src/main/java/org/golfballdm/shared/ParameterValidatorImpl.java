package org.golfballdm.shared;

import jakarta.ws.rs.core.MultivaluedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterValidatorImpl implements ParameterValidator {
    private static final String distinctFieldParameterName = Query.distinctFieldParameterName;
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
        int fieldsProcessed = 0;
        boolean fieldFound;
        boolean rtnField = false;
        do {
            String keyName = "field"+ fieldsProcessed+1;
            String valName = "value"+ fieldsProcessed+1;
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

        // Check for return_column_name
        if (parameters.containsKey(distinctFieldParameterName)) {
            String val = parameters.getFirst(distinctFieldParameterName);
            rtn.put(distinctFieldParameterName,val);
            rtnField = true;
        }

        int queryParamLength = parameters.keySet().size();
        if (queryParamLength != (fieldsProcessed*2)+(rtnField ? 1 : 0)) {
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
