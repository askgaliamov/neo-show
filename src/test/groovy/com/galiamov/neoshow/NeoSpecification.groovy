package com.galiamov.neoshow

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import groovy.json.JsonSlurper
import org.codehaus.jackson.node.TextNode
import org.neo4j.harness.ServerControls
import org.neo4j.harness.TestServerBuilders
import spock.lang.Shared
import spock.lang.Specification

import javax.ws.rs.core.MediaType

class NeoSpecification extends Specification {

    private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/"

    private static final jsonSlurper = new JsonSlurper()

    @Shared
    static final ServerControls server = TestServerBuilders.newInProcessBuilder()
            .withExtension("/unmanaged", "com.galiamov.neoshow.extension")
            .withConfig("org.neo4j.server.webserver.https.enabled", Boolean.FALSE.toString())
//                .withConfig("org.neo4j.server.database.location", "...")
            .withConfig("remote_shell_enabled", Boolean.FALSE.toString())
            .newServer()

    @Shared
    TextNode success = new TextNode("ok")

    def query(query) {
        final String txUri = SERVER_ROOT_URI + "transaction/commit"
        WebResource resource = Client.create().resource(txUri)

        String payload = "{\"statements\" : [ {\"statement\" : \"$query\"} ]}"
        ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(payload)
                .post(ClientResponse.class)

        String result = response.getEntity(String.class)

        response.close()
        jsonSlurper.parse(result.bytes)
    }

}
