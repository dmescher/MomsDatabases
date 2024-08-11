package org.golfballdm.census;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import org.golfballdm.DAO.CensusDAO;
import org.golfballdm.shared.Query;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/census")
public class CensusResource {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final CensusDAO dao = CensusDAO.getInstance();
    private static final String distinctFieldParameterName = Query.distinctFieldParameterName;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello() {
        // create a JSON string
        ObjectNode json = mapper.createObjectNode();
        json.put("result", "Census");
        return Response.status(Response.Status.OK).entity(json).build();
    }

    @GET
    @Path("/connection-test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response connectionTest() {
        boolean works = false;

        ObjectNode json = mapper.createObjectNode();

        try {
            if (dao.testConnection()) {
                json.put("result","Connection Test works");
                works = true;
            } else {
                json.put("result","Connection Test failure w/o exception");
            }
        } catch (SQLException e) {
            json.put("result","Connection Test server failure");
            json.put("exception", e.getMessage());
        }

        if (works) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
    }

    /*
     All queries through this endpoint are field#=value# AND field#=value# .....
     They apply only to free residents, not slaves
     They return the entire FreeResident model, rather than a subset of fields
     Response Body:  List<FreeResident>
     */
    @GET
    @Path("/query/free-res")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryFree(final @Context HttpHeaders headers, final @Context UriInfo uriInfo) {
        // Check authorization
        if (!authCheck()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Get query parameters and validate
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        Map<String, String> validatedParams = null;
        try {
            validatedParams = validateParameters(queryParams);
        } catch (IllegalArgumentException e) {
            ObjectNode json = mapper.createObjectNode();
            json.put("exception",e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        // Build Query object

        // Return list of persons
        return null;
    }

    /*
     All queries through this endpoint are field#=value# AND field#=value# .....
     They apply only to slaves
     They return the entire SlaveResident model, rather than a subset of fields
     Response Body:  List<SlaveResident>
     */
    @GET
    @Path("/query/slave-res")
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySlave(final @Context HttpHeaders headers, final @Context UriInfo uriInfo) {
        // Check authorization

        // Get query parameters
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        // Check to make sure all field and value parameters are matched

        // Build Query object

        // Return list of persons
        return null;
    }

    /*
     All queries through this endpoint are SELECT DISTINCT(return_field_name) FROM FreeResidents
     WHERE field#=value# AND field#=value# .....
     Response Body:  List<String>
     */
    @GET
    @Path("/query/distinct")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> queryDistinctFreeRes(final @Context HttpHeaders headers, final @Context UriInfo uriInfo) {
        // Check authorization

        // Get query parameters
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        // Check to make sure all field and value parameters are matched

        // Build Query object

        // Return list of persons
        return null;
    }

    /*
     Used for sanity checking column names to prevent SQL injection attacks.
     Since we can't use bind variables for the column names, we need to make sure
     they conform to a standard.  (In this case, alphanumeric characters, dash, and underscore only)
     */
    private boolean sanityCheckColumnName(String parameter) {
        return false;
    }

    /*
     Used for query parameter validation
     */
    private Map<String, String> validateParameters(MultivaluedMap<String, String> parameters) throws IllegalArgumentException {
        // Iterate through keys, make sure only one value per key
        for (String s: parameters.keySet()) {
            List<String> valList = parameters.get(s);
            if (valList.size() > 1) {
                throw new IllegalArgumentException("Duplicate values for parameter "+s);
            }
        }

        HashMap<String, String> rtn = new HashMap<>();

        // Parse field/value pairs in query parameters
        int paramCount = 1;
        int fieldsProcessed = 0;
        boolean fieldFound = false;
        boolean rtnField = false;
        do {
            String keyName = "field"+ paramCount;
            String valName = "value"+ paramCount;
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

        return rtn;
    }

    private boolean authCheck() {
        return false;
    }
}
