
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet(name = "autocomplete", urlPatterns = "/autocomplete")
public class autocomplete extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }


            String[] tokens = query.split(" ");
            String token = "";
            for (String t : tokens) {
                if(t.length() > 3) {
                    token = token + "+" + t + "*% ";
                }
                else{
                    token = token + t + "*% ";
                }
            }
            String movies = "SELECT id,title FROM movies WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) LIMIT 10";
            databaseAuthentication da = new databaseAuthentication();
            try{
                Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
                PreparedStatement statement = conn.prepareStatement(movies);
                statement.setString(1, token);
                ResultSet resultSet = statement.executeQuery();
                System.out.println("Executing recommendation for: "+query);
                while (resultSet.next() != false) {
                    String id = resultSet.getString("id");
                    String title = resultSet.getString("title");

                    jsonArray.add(generateJsonObject(id, title));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieID, String movieName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieName);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
