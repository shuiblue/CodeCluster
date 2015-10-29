import java.io.File;
import java.io.IOException;

/**
 * Created by shuruiz on 10/29/15.
 */
public class parseFile {

    /**
     * @param inputFile file that need to be parsed by srcML
     * @return path of XML file
     * @throws IOException e
     */
    public static String getXmlFile(String inputFile) {
        // create dir for store xml files
        String outXmlFile = "/Users/shuruiz/Work/tmpXMLFile" + inputFile.replace("testcpp", "") + ".xml";
        String[] paths = inputFile.replace("testcpp", "").split("/");
        String dir_suffix = "";
        for (int i = 1; i < paths.length - 1; i++) {
            dir_suffix += "/" + paths[i];
        }
        if (!new File(outXmlFile).exists()) {
            new File("/Users/shuruiz/Work/tmpXMLFile/" + dir_suffix).mkdirs();
        }

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
            sleep();
            doc = builder.build(file);
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }


}
