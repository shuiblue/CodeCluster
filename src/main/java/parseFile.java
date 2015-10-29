import nu.xom.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.xml.sax.*;


/**
 * Created by shuruiz on 10/29/15.
 */
public class ParseFile {
    static HashSet<CppNode> candidates = new HashSet<>();
    static int lineNum = 0;


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

    public static void parseAST(Document document) {
        Node astnode = document.getChild(0);


        int childrenNum = astnode.getChildCount();
        for (int i = 0; i < childrenNum; i++) {
            Node child = astnode.getChild(i);
            System.out.print("");

            String astNodeType = child.getClass().getName();

            if (astNodeType.contains("Text")) {
                lineNum++;
            }
            if (astNodeType.contains("Element")) {
                String localName = ((Element) child).getLocalName();
                if (localName.contains("decl_stmt")) {
                    String type = ((Element) child).getChildElements().get(0).getChildElements("type", "http://www.sdml.info/srcML/src").get(0).getValue();
                    String name = ((Element) child).getChildElements().get(0).getChildElements("name", "http://www.sdml.info/srcML/src").get(0).getValue();
                    System.out.print("");

                    candidates.add(new CppNode(name, type, localName, lineNum));
                }
            }
        }

    }

    public static void main(String args[]) {
        String inputFile = "src/testFile/A.cpp";
        String xmlPath = getXmlFile(inputFile);
        Document astTree = getXmlDom(xmlPath);
        parseAST(astTree);

    }

}
