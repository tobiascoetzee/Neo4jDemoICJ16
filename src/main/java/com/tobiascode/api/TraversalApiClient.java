package com.tobiascode.api;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TraversalApiClient {
    public static void main(String[] args) {
        GraphDatabaseSettings.BoltConnector bolt = GraphDatabaseSettings.boltConnector("0");

        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(new File("./data/graph.db"))
                .setConfig(bolt.enabled, "true")
                .setConfig(bolt.address, "localhost:7688")
                .newGraphDatabase();

        registerShutdownHook(graphDatabaseService);

        TraversalDescription traversalDescription = graphDatabaseService.traversalDescription()
                .relationships(RelationshipType.withName("MEMBER_OF"), Direction.INCOMING)
                .evaluator(Evaluators.atDepth(1))
                .uniqueness(Uniqueness.NODE_GLOBAL);

        Node startNode = getStartNode(graphDatabaseService);
        Traverser traverser = traversalDescription.traverse(startNode);

        for (Path path : traverser) {
            Node teamMember = path.endNode();
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
