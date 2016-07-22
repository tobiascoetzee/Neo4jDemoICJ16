package com.tobiascode.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestClient {
    public static void main(String[] args) {
        JSONObject parameters = new JSONObject();
        parameters.put("team", "Avengers");
        JSONObject statement = new JSONObject();
        statement.put("statement", "MATCH (c:Character)-[:MEMBER_OF]->(t:Team) WHERE t.name = {team} RETURN c.name as teammember");
        statement.put("parameters", parameters);
        JSONArray statements = new JSONArray();
        statements.put(statement);
        JSONObject cypher = new JSONObject();
        cypher.put("statements", statements);

        //        {"statements": [{
        //            "statement": "MATCH (c:Character)-[:MEMBER_OF]->(t:Team) WHERE t.name = {team} RETURN c.name as teammember",
        //            "parameters": {"team": "Avengers"}
        //        }]}

        HttpAuthenticationFeature basicAuth = HttpAuthenticationFeature.basic("neo4j", "password");

        Response response = ClientBuilder
                .newBuilder()
                .register(JacksonJsonProvider.class)
                .register(basicAuth)
                .build()
                .target("http://localhost:7474/db/data/transaction/commit")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(cypher.toString(), MediaType.APPLICATION_JSON_TYPE));

        if (response.getStatus() == 200) {
            JSONObject parser = new JSONObject(response.readEntity(String.class));

            JSONArray rows = parser.getJSONArray("results").getJSONObject(0).getJSONArray("data");

            for (int i = 0; i < rows.length(); i++) {
                JSONObject record = rows.getJSONObject(i);
                JSONArray row = record.getJSONArray("row");

                System.out.println(row.getString(0));
            }
        }

        //        {
        //            "results": [{
        //            "data": [
        //            {
        //                "meta": [null],
        //                "row": ["Yellowjacket (Rita DeMara)"]
        //            },
        //            {
        //                "meta": [null],
        //                "row": ["X-51"]
        //            },
        //            {
        //                "meta": [null],
        //                "row": ["Wonder Man"]
        //            },
        //            {
        //                "meta": [null],
        //                "row": ["Wolverine"]
        //            },
        //            "columns": ["teammember"]
        //        }],
        //            "errors": []
        //        }
    }
}
