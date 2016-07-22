package com.tobiascode.api;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CoreApiClient {
    public static void main(String[] args) {
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory()
                .newEmbeddedDatabase(new File("./data/graph.db"));

        registerShutdownHook(graphDatabaseService);

        Node startNode = getStartNode(graphDatabaseService);
        Iterable<Relationship> relationships = startNode
                .getRelationships(Direction.INCOMING, RelationshipType.withName("MEMBER_OF"));

        for (Relationship relationship : relationships) {
            Node teamMember = relationship.getStartNode();
            String teamMemberName = teamMember.getProperty("name").toString();

            System.out.println(teamMemberName);
        }
    }

    private static Node getStartNode(GraphDatabaseService graphDatabaseService) {
        Map<String, Object> params = new HashMap<>();
        params.put("team", "Avengers");

        Result result = graphDatabaseService
                .execute("MATCH (t:Team) WHERE t.name = {team} RETURN t", params);

        Iterator<Node> iter = result.columnAs("t");

        return iter.next();
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
}
