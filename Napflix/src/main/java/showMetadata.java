
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.google.gson.Gson;

@WebServlet(urlPatterns = "/ajax/metadata")
public class showMetadata extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        databaseAuthentication da = new databaseAuthentication();
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            List<String> list = new ArrayList<>();
            ResultSet metaSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while(metaSet.next()) {
                list.add(metaSet.getString("TABLE_NAME"));
                ResultSet columns = databaseMetaData.getColumns(null, null, metaSet.getString("TABLE_NAME"), null);
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    String columnSize = columns.getString("COLUMN_SIZE");
                    String concat = columnName + ", " + columnType + "(" + columnSize+")";
                    list.add(concat);
                }
                list.add("break");
            }
            Gson gson = new Gson();

            resp.getWriter().write(gson.toJson(list));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
