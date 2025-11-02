// src/main/java/graph/dagsp/DAGShortestLongest.java
package graph.dagsp;

import graph.model.WeightedGraph;
import java.util.*;

public class DAGShortestLongest {
    public int relaxCounter = 0;

    public static class PathResult {
        public final int[] dist;
        public final int[] parent;
        public PathResult(int[] dist, int[] parent) {
            this.dist = dist;
            this.parent = parent;
        }
    }

    public PathResult shortestPath(WeightedGraph dag, List<Integer> topoOrder, Map<Integer, Integer> nodeToIndex) {
        int n = dag.n;
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        int src = dag.source;
        dist[src] = 0;

        for (int u : topoOrder) {
            if (dist[u] == Integer.MAX_VALUE) continue;
            for (var edge : dag.adj.get(u)) {
                int v = edge.to;
                int w = edge.weight;
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                    relaxCounter++;
                }
            }
        }
        return new PathResult(dist, parent);
    }

    public PathResult longestPath(WeightedGraph dag, List<Integer> topoOrder, Map<Integer, Integer> nodeToIndex) {
        // Invert weights and run shortest path
        WeightedGraph negGraph = new WeightedGraph(dag.n, true, "edge", dag.source);
        for (int u = 0; u < dag.n; u++) {
            for (var e : dag.adj.get(u)) {
                negGraph.addEdge(u, e.to, -e.weight);
            }
        }

        PathResult negResult = shortestPath(negGraph, topoOrder, nodeToIndex);
        int[] longestDist = new int[dag.n];
        for (int i = 0; i < dag.n; i++) {
            longestDist[i] = negResult.dist[i] == Integer.MAX_VALUE ? Integer.MIN_VALUE : -negResult.dist[i];
        }
        return new PathResult(longestDist, negResult.parent);
    }

    public List<Integer> reconstructPath(int start, int end, int[] parent) {
        List<Integer> path = new ArrayList<>();
        for (int at = end; at != -1; at = parent[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}