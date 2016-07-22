package com.tobiascode.bolt;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;

import java.util.HashMap;
import java.util.Map;

public class BoltClient {
    public static void main(String[] args) {
        try (Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "password"))) {
            try (Session session = driver.session()) {
                Map<String, Object> params = new HashMap<>();
                params.put("team", "Avengers");

                StatementResult result = session
                        .run("MATCH (c:Character)-[:MEMBER_OF]->(t:Team) WHERE t.name = {team} RETURN c as teammember", params);

                while (result.hasNext()) {
                    Record record = result.next();

                    Node node = record.get("teammember").asNode();
                    String teamMemberName = node.get("name").asString();

                    System.out.println(teamMemberName);
                }
            }
        }
    }
}
