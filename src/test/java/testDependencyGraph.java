import edu.uci.ics.jung.graph.DirectedSparseGraph;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
/**
 * Created by shuruiz on 10/30/15.
 */
public class testDependencyGraph {

    @Test
    public void test1(){
        String inputFile = "src/testFile/A.cpp";

        ParseFile parseFile = new ParseFile();
        DirectedSparseGraph<Integer, Edge> g =parseFile.codeCluster(inputFile);
        assertTrue(g.getEdgeCount()==5);
    }

}
