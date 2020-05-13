import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CastParser {
    Set<Cast> casts = new HashSet<>();
    public void parseDom(Document dom) {
        int maxID = Integer.parseInt(getMaxID().substring(2));
        Element element = dom.getDocumentElement();
        NodeList nodeList = element.getElementsByTagName("actor");
        for(int i = 0; i < nodeList.getLength(); i++){
            String id = "nm" + Integer.toString(maxID + 1 + i);
            Element cur = (Element) nodeList.item(i);
            int dob = -1;
            String stageName = null;
            if (cur.getElementsByTagName("dob").getLength() > 0 && canStringBeInt(cur.getElementsByTagName("dob").item(0).getTextContent())){
                dob = Integer.parseInt(cur.getElementsByTagName("dob").item(0).getTextContent());
            }else{
                System.out.println(cur.getTextContent() + " didn't have a valid dob");
                continue;
            }
            if (cur.getElementsByTagName("stagename").getLength() > 0){
                stageName = cur.getElementsByTagName("stagename").item(0).getTextContent();
            }else{
                System.out.println(cur.getTextContent() + " didn't have a valid stagename");
                continue;
            }
            Cast c = new Cast(id, stageName, dob);
            casts.add(c);
        }
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            databaseAuthentication da = new databaseAuthentication();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            PreparedStatement statement = conn.prepareStatement("");
            for (Cast c: new ArrayList<>(this.casts)) {
                String s = "INSERT INTO stars SELECT\"" + c.getId() + "\",\"" + c.getName() + "\",\"" + Integer.toString(c.getBirthYear()) + "\" WHERE \"" + c.getName() + "\" NOT IN (SELECT name FROM stars)";
                statement.addBatch(s);
            }
            statement.addBatch("CREATE INDEX starName ON stars(name)");
            statement.executeBatch();
            statement.close();
        }catch (Exception e){}


    }


    public boolean canStringBeInt(String s){
        try{
            Integer.parseInt(s);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String getMaxID(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            databaseAuthentication da = new databaseAuthentication();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            Statement statement = conn.createStatement();
            ResultSet r =  statement.executeQuery("SELECT ID FROM stars ORDER BY ID DESC Limit 1;");
            r.next();
            String temp = r.getString(1);
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
