package org.golfballdm.census;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import org.golfballdm.DAO.CensusDAO;
import org.golfballdm.models.FreeResident;
import org.golfballdm.models.SlaveResident;

import java.sql.SQLException;

@Path("/census")
public class CensusResource {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final CensusDAO dao = CensusDAO.getInstance();

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

        ObjectNode json = mapper.createObjectNode();
        json.put("result","Connection Test works");

        try {
            if (dao.testConnection()) {
                json.put("result","Connection Test works");
            } else {
                json.put("result","Connection Test failure w/o exception");
            }
        } catch (SQLException e) {
            json.put("result","Connection Test server failure");
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
    }

    /*
     All queries through this endpoint are field#=value# AND field#=value# .....
     They apply only to free residents, not slaves
     */
    @GET
    @Path("/query/free-res")
    @Produces(MediaType.APPLICATION_JSON)
    public FreeResident[] queryFree(final @Context HttpHeaders headers, final @Context UriInfo uriInfo) {
        // Get session/token code from headers

        // Get query parameters
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        // Check to make sure all field and value parameters are matched

        // Build Query object

        // Return list of persons
        return null;
    }

    @GET
    @Path("/query/slave-res")
    @Produces(MediaType.APPLICATION_JSON)
    public SlaveResident[] querySlave(final @Context HttpHeaders headers, final @Context UriInfo uriInfo) {
        // Get session/token code from headers

        // Get query parameters
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        // Check to make sure all field and value parameters are matched

        // Build Query object

        // Return list of persons
        return null;
    }

}
