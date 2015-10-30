
import java.util.HashSet;

/**
 * Created by shuruiz on 10/29/15.
 */
public class CppNode {


    int lineNumber;
    String type;
    String name;
    String localName;
    HashSet<CppNode> dependencies;



    HashSet<CppNode> sameNameList;

    CppNode(String name, String type,String localName,int lineNumber) {
        this.name = name;
        this.type = type;
        this.localName=localName;
        this.lineNumber=lineNumber;
        dependencies = new HashSet<>();
        sameNameList = new HashSet<>();
    }
    public int getLineNumber() {
        return lineNumber;
    }




    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getLocalName() {
        return localName;
    }

    public HashSet<CppNode> getDependencies() {
        return dependencies;
    }
    public HashSet<CppNode> getSameNameList() {
        return sameNameList;
    }


}
