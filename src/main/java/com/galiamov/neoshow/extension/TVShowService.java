package com.galiamov.neoshow.extension;

import com.galiamov.neoshow.NeoShowException;
import com.galiamov.neoshow.model.Like;
import com.galiamov.neoshow.model.Pageable;
import com.galiamov.neoshow.model.TVShow;
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
import java.util.Date;
import java.util.List;

import static com.galiamov.neoshow.extension.NeoShowObjectMapper.parseDate;
import static com.galiamov.neoshow.extension.ResponseWriter.error;
import static com.galiamov.neoshow.model.Validator.validateEmail;

@Path("/tv_shows")
public class TVShowService {

    private final Neo4JRepository repository;

    public TVShowService(@Context GraphDatabaseService database) {
        this.repository = Neo4JRepository.getInstance(database);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response create(String requestBody) {
        try {
            TVShow tvShowRequest = NeoShowObjectMapper.readValue(requestBody, TVShow.class);
            repository.createTVShow(tvShowRequest);
            return ResponseWriter.ok();
        } catch (NeoShowException e) {
            return error(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getAllTVShows(@QueryParam("skip") Integer skip, @QueryParam("limit") Integer limit) {
        try {
            Pageable pageable = new Pageable(skip, limit);
            List<TVShow> allTVShows = repository.getAllTVShows(pageable);
            return ResponseWriter.write(allTVShows);
        } catch (NeoShowException e) {
            return error(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/like")
    public Response like(String requestBody) {
        try {
            Like like = NeoShowObjectMapper.readValue(requestBody, Like.class);
            repository.likeTVShow(like);
            return ResponseWriter.ok();
        } catch (NeoShowException e) {
            return error(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/aired_on/{date}")
    public Response getAiredOn(@PathParam("date") String date, @QueryParam("skip") Integer skip,
            @QueryParam("limit") Integer limit) {
        try {
            Pageable pageable = new Pageable(skip, limit);
            Date time = parseDate(date);
            List<TVShow> airedOn = repository.getAiredOn(time, pageable);
            return ResponseWriter.write(airedOn);
        } catch (NeoShowException e) {
            return error(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/recommendation/{email}")
    public Response getRecommendationByCoLike(@PathParam("email") String email, @QueryParam("skip") Integer skip,
            @QueryParam("limit") Integer limit) {
        try {
            Pageable pageable = new Pageable(skip, limit);
            validateEmail(email);
            List<TVShow> airedOn = repository.getCoLikeRecommendation(email, pageable);
            return ResponseWriter.write(airedOn);
        } catch (NeoShowException e) {
            return error(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/top")
    public Response getRecommendationTop(@QueryParam("skip") Integer skip, @QueryParam("limit") Integer limit) {
        try {
            Pageable pageable = new Pageable(skip, limit);
            List<TVShow> airedOn = repository.getTopLikedShow(pageable);
            return ResponseWriter.write(airedOn);
        } catch (NeoShowException e) {
            return error(e);
        }
    }

}
