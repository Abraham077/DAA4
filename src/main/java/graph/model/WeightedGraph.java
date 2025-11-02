// src/main/java/graph/model/WeightedGraph.java
package graph.model;

import java.util.*;

public class WeightedGraph {
    public final int n;
    public final boolean directed;
    public final List<List<Edge>> adj;
    public final String weightModel; // "edge" or "node"
    public final int source;

    public static class Edge {
        public final int to;
        public final int weight;
        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public WeightedGraph(int n, boolean directed, String weightModel, int source) {
        this.n = n;
        this.directed = directed;
        this.weightModel = weightModel;
        this.source = source;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
        if (!directed) {
            adj.get(v).add(new Edge(u, w));
        }
    }
}