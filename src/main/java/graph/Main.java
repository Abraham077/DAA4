// src/main/java/graph/Main.java
package graph;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import graph.model.WeightedGraph;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopoSort;
import graph.dagsp.DAGShortestLongest;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String filename = args.length > 0 ? args[0] : "data/tasks.json";
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(new FileReader(filename), JsonObject.class);

        int n = root.get("n").getAsInt();
        boolean directed = root.get("directed").getAsBoolean();
        String weightModel = root.get("weight_model").getAsString();
        int source = root.get("source").getAsInt();

        WeightedGraph graph = new WeightedGraph(n, directed, weightModel, source);

        JsonArray edges = root.getAsJsonArray("edges");
        for (var e : edges) {
            int u = e.getAsJsonObject().get("u").getAsInt();
            int v = e.getAsJsonObject().get("v").getAsInt();
            int w = e.getAsJsonObject().get("w").getAsInt();
            graph.addEdge(u, v, w);
        }

        // SCC
        long start = System.nanoTime();
        TarjanSCC sccFinder = new TarjanSCC(graph);
        List<List<Integer>> sccs = sccFinder.findSCCs();
        long sccTime = System.nanoTime() - start;

        System.out.println("SCCs:");
        for (var comp : sccs) {
            System.out.println(comp + " (size: " + comp.size() + ")");
        }

        // Topo
        KahnTopoSort topo = new KahnTopoSort();
        List<Integer> compTopo = topo.topoSort(sccs, graph);

        // Build node-to-component map
        Map<Integer, Integer> nodeToComp = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (int node : sccs.get(i)) {
                nodeToComp.put(node, i);
            }
        }

        // Topological order of original nodes
        List<Integer> nodeTopo = new ArrayList<>();
        for (int compId : compTopo) {
            nodeTopo.addAll(sccs.get(compId));
        }

        System.out.println("Topo order (components): " + compTopo);
        System.out.println("Topo order (nodes): " + nodeTopo);

        // DAG Shortest/Longest
        DAGShortestLongest dag = new DAGShortestLongest();
        var shortest = dag.shortestPath(graph, nodeTopo, nodeToComp);
        var longest = dag.longestPath(graph, nodeTopo, nodeToComp);

        System.out.println("Shortest distances from " + source + ": " + Arrays.toString(shortest.dist));
        System.out.println("Longest distances: " + Arrays.toString(longest.dist));

        // Find critical path (longest)
        int maxDist = Integer.MIN_VALUE;
        int criticalEnd = -1;
        for (int i = 0; i < n; i++) {
            if (longest.dist[i] > maxDist) {
                maxDist = longest.dist[i];
                criticalEnd = i;
            }
        }
        if (criticalEnd != -1) {
            var criticalPath = dag.reconstructPath(source, criticalEnd, longest.parent);
            System.out.println("Critical path: " + criticalPath + " (length: " + maxDist + ")");
        }

        // Metrics
        System.out.println("\n=== METRICS ===");
        System.out.println("SCC DFS visits: " + sccFinder.dfsCounter);
        System.out.println("Topo push/pop ops: " + topo.pushPopCounter);
        System.out.println("Relaxations: " + dag.relaxCounter);
        System.out.println("SCC Time (ns): " + sccTime);
    }
}