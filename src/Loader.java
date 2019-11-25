import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

public class Loader
{
    public static void main(String[] args) throws Exception
    {
        long usage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long start = System.currentTimeMillis();
        String fileName = "res/data-18M.xml";
        parseFile(fileName);
        XMLHandler.executeMultiInsert();
        System.out.println("Parsing duration - " + (System.currentTimeMillis() - start) + " ms");

        DBConnection.printVoterCounts();

        usage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - usage;
        System.out.println(usage);
    }

    private static void parseFile(String fileName) throws Exception
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLHandler handler = new XMLHandler();
        parser.parse(new File(fileName), handler);
    }
}