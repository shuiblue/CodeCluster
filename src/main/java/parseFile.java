
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import nu.xom.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by shuruiz on 10/29/15.
 */
public class ParseFile {
    static List<CppNode> candidates = new ArrayList<>();
    static int lineNum = 1;
    static Entity entity = new Entity();
    static DirectedSparseGraph<Integer, String> g = new DirectedSparseGraph<Integer, String>();

    /**
     * @param inputFile file that need to be parsed by srcML
     * @return path of XML file
     * @throws IOException e
     */
    public static String getXmlFile(String inputFile) {
        // create dir for store xml files
        String outXmlFile = inputFile.replace("testcpp", "") + ".xml";
        //run srcML
        if (new File(inputFile).isFile()) {
            try {
                new ProcessBuilder("/usr/local/bin/src2srcml",
                        inputFile, "-o", outXmlFile).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File does not exist: " + inputFile);
        }
        return outXmlFile;
    }

    /**
     * parse xml file to DOM.
     *
     * @param xmlFilePath path of xml file
     */
    public static Document getXmlDom(String xmlFilePath) {
        Document doc = null;
        try {
            Builder builder = new Builder();
            File file = new File(xmlFilePath);
            doc = builder.build(file);

        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * parse AST to add CppNode into candidates
     *
     * @param astNode
     */
    public static void parseAST(Node astNode) {

        int childrenNum = astNode.getChildCount();
        for (int i = 0; i < childrenNum; i++) {
            Node child = astNode.getChild(i);
            String astNodeType = child.getClass().getName();

            if (astNodeType.contains("Text")) {
                if (child.getValue().contains("\n")) {
                    lineNum++;
                }
            }
            if (astNodeType.contains("Element")) {
                String localName = ((Element) child).getLocalName();
                if (localName.equals("decl_stmt")) {
                    String type = ((Element) child).getChildElements().get(0).getChildElements("type", "http://www.sdml.info/srcML/src").get(0).getValue();
                    String name = ((Element) child).getChildElements().get(0).getChildElements("name", "http://www.sdml.info/srcML/src").get(0).getValue();
                    candidates.add(new CppNode(name, type, localName, lineNum));
                } else if (localName.equals("function_decl")) {
                    String type = ((Element) child).getChildElements().get(0).getValue();
                    String name = ((Element) child).getChildElements().get(1).getValue();
                    candidates.add(new CppNode(name, type, localName, lineNum));
                } else if (localName.equals("function")) {
                    String type = ((Element) child).getChildElements().get(0).getValue();
                    String name = ((Element) child).getChildElements().get(1).getValue();
                    candidates.add(new CppNode(name, type, localName, lineNum));
                    parseAST(child);
                } else if (localName.equals("block")) {
                    parseAST(child);
                } else if (localName.equals("expr_stmt")) {
                    Element exprChild = ((Element) ((Element) child).getChildElements().get(0).getChild(0));
                    String exprType = exprChild.getLocalName();
                    if (exprType.equals("name")) {
                        String name = exprChild.getValue();
                        String type = "";
                        candidates.add(new CppNode(name, type, localName, lineNum));
                    } else if (exprType.equals("call")) {
                        String name = exprChild.getChild(0).getValue();
                        String type = "call";
                        candidates.add(new CppNode(name, type, localName, lineNum));
                        parseAST(exprChild);
                    }
                } else if (localName.equals("call")) {
                    String name = child.getChild(0).getValue();
                    String type = "call";
                    candidates.add(new CppNode(name, type, localName, lineNum));
                    parseAST(child);
                } else if (localName.equals("argument")) {
                    Element c = ((Element) ((Element) child).getChildElements().get(0).getChild(0));
                    if (c.getLocalName().equals("name")) {
                        String name = c.getValue();
                        String type = "";
                        candidates.add(new CppNode(name, type, localName, lineNum));
                    } else {
                        parseAST(child);
                    }


                } else if (!entity.getTerminal().contains(localName)) {
                    parseAST(child);
                }

            }
        }

    }

    public static void dependencyGraph() {

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
                            g.addEdge(type_2 + " " + name_2, line_2, line_1);

                        } else if (local_2.contains("decl")) {
                            candidates.get(i).getDependencies().add(candidates.get(t));
                            g.addEdge(type_1 + " " + name_1, line_1, line_2);
                        } else {
                            candidates.get(i).sameNameList.add(candidates.get(t));
                            candidates.get(t).sameNameList.add(candidates.get(i));

                        }

                    }
                }
            }

        }
    }

    public static void visualizeGraph() {
        VisualizationImageServer<Integer, String> vs =
                new VisualizationImageServer<Integer, String>(new CircleLayout<Integer, String>(g), new Dimension(200, 200));

        JFrame frame = new JFrame();
        frame.getContentPane().add(vs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]) {
        String inputFile = "src/testFile/A.cpp";
        String xmlPath = getXmlFile(inputFile);
        Document astTree = getXmlDom(xmlPath);
        parseAST(astTree.getChild(0));
        dependencyGraph();


        System.out.print("");
    }

}
