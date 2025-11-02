// src/main/java/graph/topo/KahnTopoSort.java
package graph.topo;

import graph.scc.Component;
import graph.model.WeightedGraph;
import java.util.*;

public class KahnTopoSort {
    public int pushPopCounter = 0;

    public List<Integer> topoSort(List<List<Integer>> sccs, WeightedGraph originalGraph) {
        int compCount = sccs.size();
        Map<Integer, Integer> nodeToComp = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (int node : sccs.get(i)) {
                nodeToComp.put(node, i);
            }
        }

        List<Set<Integer>> condAdj = new ArrayList<>();
        for (int i = 0; i < compCount; i++) condAdj.add(new HashSet<>());

        for (int u = 0; u < originalGraph.n; u++) {
            for (var edge : originalGraph.adj.get(u)) {
                int v = edge.to;
                int compU = nodeToComp.get(u);
                int compV = nodeToComp.get(v);
                if (compU != compV) {
                    condAdj.get(compU).add(compV);
                }
            }
        }

        // Kahn's algorithm
        int[] inDegree = new int[compCount];
        for (int u = 0; u < compCount; u++) {
            for (int v : condAdj.get(u)) {
                inDegree[v]++;
            }
        }

        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < compCount; i++) {
            if (inDegree[i] == 0) {
                q.offer(i);
                pushPopCounter++;
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            pushPopCounter++;
            topoOrder.add(u);
            for (int v : condAdj.get(u)) {
                if (--inDegree[v] == 0) {
                    q.offer(v);
                    pushPopCounter++;
                }
            }
        }

        return topoOrder;
    }
}