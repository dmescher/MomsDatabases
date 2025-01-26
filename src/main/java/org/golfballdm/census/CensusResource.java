package org.golfballdm.census;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import org.golfballdm.DAO.CensusDAO;
import org.golfballdm.orm.FreeResMapper;
import org.golfballdm.shared.ParameterValidator;
import org.golfballdm.shared.ParameterValidatorImpl;
import org.golfballdm.shared.Query;
import org.golfballdm.shared.QueryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/census")
public class CensusResource {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final CensusDAO dao = CensusDAO.getInstance();
    private static final ParameterValidator parameterValidator = new ParameterValidatorImpl();
    private static final Logger logger = LoggerFactory.getLogger(CensusResource.class);
    private static final Map<Integer, String> expressQueries;

    static {
        expressQueries = new HashMap<>();
        expressQueries.put(0, "SELECT * FROM dbo.FreeResidents WHERE NAME = ?");
        expressQueries.put(1, "SELECT * FROM dbo.FreeResidents WHERE FamilyID = ?");
        expressQueries.put(2, "SELECT * FROM dbo.FreeResidents WHERE NumSlaves = ?");
        expressQueries.put(3, "SELECT * FROM dbo.FreeResidents WHERE "); // TODO:  County
        expressQueries.put(4, "SELECT * FROM dbo.FreeResidents WHERE Color = ?");
        expressQueries.put(5, "SELECT * FROM dbo.FreeResidents WHERE Married = ?");
        expressQueries.put(6, "SELECT * FROM dbo.FreeResidents WHERE Schooling = ?");
        expressQueries.put(7, "SELECT * FROM dbo.FreeResidents WHERE IlliterateOver20 = ?");
    }

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
        logger.info("entering query (free-res) endpoint");

        // Check authorization
        if (!authCheck()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        logger.info("authCheck passed");


        // Get query parameters and validate
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        Map<String, String> validatedParams = null;
        try {
            validatedParams = parameterValidator.validateParameters(queryParams);
        } catch (IllegalArgumentException e) {
            ObjectNode json = mapper.createObjectNode();
            json.put("exception",e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        boolean express = validatedParams.containsKey(Query.expressQuery);

        logger.info("parameter validation passed");

        FreeResMapper ORMmapper = new FreeResMapper();
        QueryExecutor exec = (express) ?
                new QueryExecutor(dao, validatedParams, new String[]{"dbo.FreeResidents"}, ORMmapper) :
                new QueryExecutor(dao, validatedParams, new String[]{"dbo.FreeResidents"}, expressQueries, ORMmapper);
        // Build Query
        try {
            if (!express) {
                exec.buildQuery();
            } else {
                exec.buildExpressQuery(Integer.valueOf(validatedParams.get(Query.expressQuery)));
            }
        } catch (SQLException e) {
            ObjectNode json = mapper.createObjectNode();
            json.put("exception", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        List<Object> rtnVal = null;
        try {
            rtnVal =  exec.executeQuery();
        } catch (SQLException e) {
            ObjectNode json = mapper.createObjectNode();
            json.put("exception", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }

        ArrayNode rtnList = mapper.createArrayNode();
        for (Object o : rtnVal) {
            rtnList.addPOJO(o);
        }

        return Response.status(Response.Status.OK).entity(rtnList).build();
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

    // Query list for DB
    // * Name
    // * Family ID
    // * # of slaves
    // * County
    // * Color
    // * Married in Last Year
    // * Schooling in Last Year
    // * Over 20 & Cannot Read/Write


    private boolean authCheck() {
        // Probably will get moved to its own setup in the shared directory
        return true;
    }
}
