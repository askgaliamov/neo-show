package com.galiamov.neoshow.extension;

import com.galiamov.neoshow.NeoShowException;
import com.galiamov.neoshow.model.Pageable;
import com.galiamov.neoshow.model.TVShow;
import com.galiamov.neoshow.model.User;
import com.galiamov.neoshow.repository.Neo4JRepository;
import org.neo4j.graphdb.GraphDatabaseService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

import static com.galiamov.neoshow.extension.NeoShowObjectMapper.readValue;
import static com.galiamov.neoshow.extension.ResponseWriter.error;
import static com.galiamov.neoshow.extension.ResponseWriter.write;

@Path("/users")
public class UserService {

    private final Neo4JRepository repository;

    public UserService(@Context GraphDatabaseService database) {
        repository = Neo4JRepository.getInstance(database);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createUser(String requestBody) {
        try {
            User user = readValue(requestBody, User.class);
            repository.createUser(user);
            return ResponseWriter.ok();
        } catch (NeoShowException e) {
            return error(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getAllUsers(@QueryParam("skip") Integer skip, @QueryParam("limit") Integer limit) {
        try {
            Pageable pageable = new Pageable(skip, limit);
            Collection<User> users = repository.getUsers(pageable);
            return write(users);
        } catch (NeoShowException e) {
            return error(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{email}/likes")
    public Response getUserLikes(@PathParam("email") String email, @QueryParam("skip") Integer skip,
            @QueryParam("limit") Integer limit) {
        try {
            Pageable pageable = new Pageable(skip, limit);
            Collection<TVShow> userLikes = repository.findUserLikes(email, pageable);
            return write(userLikes);
        } catch (NeoShowException e) {
            return error(e);
        }
    }

}
