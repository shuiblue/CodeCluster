import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.List;


/**
 * Created by shuruiz on 10/30/15.
 */
public class DependencyGraph {

    public  DirectedSparseGraph<Integer, Edge> createDependencyGraph(List<CppNode> candidates) {
        DirectedSparseGraph<Integer, Edge> g = new DirectedSparseGraph<>();
        for (CppNode c : candidates) {
            g.addVertex(c.getLineNumber());
        }
        for (int i = 0; i < candidates.size(); i++) {
            String name_1 = candidates.get(i).getName();
            String type_1 = candidates.get(i).getType();
            String local_1 = candidates.get(i).getLocalName();
            int line_1 = candidates.get(i).getLineNumber();

            for (int t = 0; t < candidates.size(); t++) {
                if (i != t) {
                    String name_2 = candidates.get(t).getName();
                    String type_2 = candidates.get(t).getType();
                    String local_2 = candidates.get(t).getLocalName();
                    int line_2 = candidates.get(t).getLineNumber();
                    if (name_1.equals(name_2)) {
                        if (local_1.contains("decl")) {
                            candidates.get(t).getDependencies().add(candidates.get(i));
                            g.addEdge(new Edge(type_2 + " " + name_2), line_2, line_1);

                        } else if (local_2.contains("decl")) {
                            candidates.get(i).getDependencies().add(candidates.get(t));
                            g.addEdge(new Edge(type_1 + "  " + name_1), line_1, line_2);
                        } else {
                            candidates.get(i).sameNameList.add(candidates.get(t));
                            candidates.get(t).sameNameList.add(candidates.get(i));

                        }

                    }
                }
            }

        }
        return g;
    }


}
