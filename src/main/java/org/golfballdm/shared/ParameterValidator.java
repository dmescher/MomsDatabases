package org.golfballdm.shared;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Map;

public interface ParameterValidator {
    /*
     Used for validating the query parameters
     */
    Map<String, String> validateParameters(MultivaluedMap<String, String> parameters);
}
