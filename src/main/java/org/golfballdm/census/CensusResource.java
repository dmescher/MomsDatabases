package org.golfballdm.census;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.golfballdm.DAO.CensusDAO;

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

        return Response.status(Response.Status.OK).entity(json).build();

    }

}
