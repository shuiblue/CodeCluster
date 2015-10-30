import java.util.HashSet;
import java.util.Set;

/**
 * Created by shuruiz on 10/29/15.
 */
public class Entity {
    public Set<String> getTerminal() {
        return terminal;
    }

    Set<String> terminal ;
    public  Entity(){
        terminal= new HashSet<>();
        terminal.add("type");
        terminal.add("name");
        terminal.add("parameter_list");

    }

}
