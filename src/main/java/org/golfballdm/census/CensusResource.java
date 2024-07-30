package org.golfballdm.census;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.golfballdm.DAO.CensusDAO;

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

}
