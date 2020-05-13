import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.*;

public class MovieCastParser {
    public void parseDom(Document dom) {
        Element element = dom.getDocumentElement();
        NodeList nodeList = element.getElementsByTagName("m");
        System.out.println(getActorID("Fred Astaire"));
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            databaseAuthentication da = new databaseAuthentication();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO stars_in_movies VALUES (?,?)");
            for(int i = 0; i < nodeList.getLength();i++){
                Element elem = (Element) nodeList.item(i);
                String actorName = null;
                String movieID = null;
                if (elem.getElementsByTagName("f").getLength() > 0){
                    movieID = elem.getElementsByTagName("f").item(0).getTextContent();
                }else{
                    System.out.println(elem.getTextContent() +  " does not have movieID");
                    continue;
                }
                if(elem.getElementsByTagName("a").getLength() > 0){
                    actorName = elem.getElementsByTagName("a").item(0).getTextContent();
                }else{
                    System.out.println(elem.getTextContent() +  " does not have actorName");
                    continue;
                }
                String actorID = getActorID(actorName);
                preparedStatement.setString(1,actorID);
                preparedStatement.setString(2,movieID);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getActorID(String actorName){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            databaseAuthentication da = new databaseAuthentication();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            Statement statement = conn.createStatement();
            ResultSet r =  statement.executeQuery("SELECT ID FROM stars WHERE name = \"" + actorName + "\"");
            String temp = "";
            if(r.next()){
                temp = r.getString(1);
            }
            r.close();
            statement.close();
            conn.close();
            return temp;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "-1";
    }
}
