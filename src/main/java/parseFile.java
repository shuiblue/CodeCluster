
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import nu.xom.*;

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

    public DirectedSparseGraph<Integer, Edge> codeCluster(String inputFile) {

        String xmlPath = getXmlFile(inputFile);
        Document astTree = getXmlDom(xmlPath);
        parseAST(astTree.getChild(0));
        DirectedSparseGraph<Integer, Edge> g = new DependencyGraph().createDependencyGraph(candidates);
        new VisualizeGraph(g);
        return g;
    }

    public static void main(String args[]) {
        String inputFile = "src/testFile/A.cpp";
        String xmlPath = getXmlFile(inputFile);
        Document astTree = getXmlDom(xmlPath);
        parseAST(astTree.getChild(0));
        DirectedSparseGraph<Integer, Edge> g = new DependencyGraph().createDependencyGraph(candidates);
        new VisualizeGraph(g);
    }

}
