// src/main/java/graph/scc/TarjanSCC.java
package graph.scc;

import graph.model.WeightedGraph;
import java.util.*;

public class TarjanSCC {
    private final WeightedGraph graph;
    private int time = 0;
    private final int[] disc, low;
    private final boolean[] onStack;
    private final Stack<Integer> stack;
    private final List<List<Integer>> sccList = new ArrayList<>();
    public int dfsCounter = 0;

    public TarjanSCC(WeightedGraph graph) {
        this.graph = graph;
        int n = graph.n;
        disc = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        Arrays.fill(disc, -1);
    }

    public List<List<Integer>> findSCCs() {
        for (int i = 0; i < graph.n; i++) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }
        return sccList;
    }

    private void dfs(int u) {
        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;
        dfsCounter++;

        for (var edge : graph.adj.get(u)) {
            int v = edge.to;
            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != u);
            sccList.add(component);
        }
    }
}