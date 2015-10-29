import nu.xom.Document;
import nu.xom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuruiz on 10/29/15.
 */
public class CppNode {
    int lineNumber;
    Node node;
    String name;
    String type;
    String localName;
    List<Integer> dependencies;


    CppNode(String name, String type,String localName,int lineNumber) {
        this.name = name;
        this.type = type;
        this.localName=localName;
        dependencies = new ArrayList<>();
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
