import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLHandler extends DefaultHandler {
    private static final String S_S_1 = "%s('%s', '%s', 1)";
    private static final String SQL_INSERT = "INSERT INTO voter_count(name, birthDate, `count`) VALUES %s ON DUPLICATE KEY UPDATE `count` = `count` + 1";

    private static StringBuffer insertQuery = new StringBuffer();
    private static String insertQueryEmptyPrefix = "";
    private static String insertQueryCommaPrefix = ",";

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes){
        try {
            if (qName.equals("voter")) {
                countVoter(attributes.getValue("name"),
                        attributes.getValue("birthDay").replace(".", "-"));
            }

        } catch (Exception ex) {}
    }

    public static void countVoter(String name, String birthDay) throws SQLException
    {
        insertQuery.append(String.format(S_S_1,
        insertQuery.length() == 0 ? insertQueryEmptyPrefix : insertQueryCommaPrefix, name, birthDay));

        if (insertQuery.length() >= 10000) {
            executeMultiInsert();
            insertQuery.setLength(0);
        }
    }

    public static void executeMultiInsert() throws SQLException{
        DBConnection.getConnection().createStatement().execute(String.format(SQL_INSERT,
                insertQuery.toString()));

    }
}
