package com.galiamov.neoshow.extension;

import com.galiamov.neoshow.NeoShowException;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class ResponseWriter {

    private static Response OK = Response.status(200).entity("{ \"result\": \"ok\" }").type(APPLICATION_JSON).build();

    static Response ok() {
        return OK;
    }

    static Response success(String text) {
        return Response.status(200).entity(text).type(APPLICATION_JSON).build();
    }

    static Response write(Object object) {
        return success(NeoShowObjectMapper.writeValueAsString(object));
    }

    static Response error(NeoShowException e) {
        return Response.status(200).entity(
                "{\"result\":\"error\",\"reason\":\"" + e.getReason() + "\"}").type(APPLICATION_JSON).build();
    }
}
