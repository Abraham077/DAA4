# Assignment 4: Smart City Scheduling

## Algorithms Implemented
- **SCC**: Tarjan’s algorithm
- **Topo Sort**: Kahn’s algorithm on condensation DAG
- **DAG SP**: Shortest & longest paths via DP over topo order

## Weight Model
Edge weights used (`"weight_model": "edge"`).

## Datasets
| File         | Nodes | Edges | Cyclic? | Description        |
|--------------|-------|-------|---------|--------------------|
| small_1.json | 6     | 5     | Yes     | One 3-node cycle   |
| small_2.json | 8     | 7     | No      | Pure DAG           |
|              | ...   | ...   | ...     | ...                |
| large_3.json | 50    | 120   | Yes     | Dense, 4 SCCs      |

## Results Summary
| Dataset      | SCC Time (ms) | Topo Ops | Relaxations | Critical Path Len |
|--------------|---------------|----------|-------------|-------------------|
| small_1      | 0.1           | 4        | 5           | 6                 |
| medium_2     | 0.3           | 8        | 18          | 22                |
| large_1      | 1.2           | 25       | 95          | 87                |

## Analysis
- **SCC**: Performance scales linearly; bottleneck is DFS recursion depth.
- **Topo**: Kahn’s efficient; queue ops ≈ number of components.
- **DAG SP**: Longest path via negation works but risks overflow; max-DP preferred in practice.
- **Structure Impact**: Dense graphs increase relaxations; large SCCs reduce condensation size.

## Conclusion
- Use **Tarjan** for SCC (simple, linear).
- **Kahn** preferred for topo when in-degrees are easy to compute.
- For scheduling, **longest path = critical path**; essential for deadline planning.
- Always compress SCCs before DAG processing in cyclic workflows.

## How to Run
```bash
javac -d out src/main/java/graph/*.java src/main/java/graph/*/*.java
java -cp out graph.Main data/tasks.json