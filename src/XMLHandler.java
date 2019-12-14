import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.PreparedStatement;
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

    private static PreparedStatement statement;
    private static int counter;

    public XMLHandler() {
        try {
            DBConnection.getConnection().setAutoCommit(false);
            String sql = "INSERT INTO voter_count(name, birthDate) VALUES(?, ?)";
            statement = DBConnection.getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
        statement.setString(1, name); //заполняем
        statement.setString(2, birthDay);
        statement.addBatch(); //добавляем в пакет
        counter++;
        if (counter % 40_000 == 0) { //каждые 40_000 записей отправляем в бд
            flushCounter();
        }

//        insertQuery.append(String.format(S_S_1,
//        insertQuery.length() == 0 ? insertQueryEmptyPrefix : insertQueryCommaPrefix, name, birthDay));
//
//        if (insertQuery.length() >= 10000) {
//            executeMultiInsert();
//            insertQuery.setLength(0);
//        }
    }

    public static void flushCounter() throws SQLException {
        statement.executeBatch();
        DBConnection.getConnection().commit();
    }

    public static void executeMultiInsert() throws SQLException{
        DBConnection.getConnection().createStatement().execute(String.format(SQL_INSERT,
                insertQuery.toString()));

    }
}
