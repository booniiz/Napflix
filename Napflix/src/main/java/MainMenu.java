import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "MainMenu", urlPatterns = "/ajax/main")
public class MainMenu extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        HttpSession session = req.getSession();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String query = "SELECT name FROM genres";
        List<String> genres = new ArrayList<>();
        Movie cur = new Movie();

        databaseAuthentication da = new databaseAuthentication();
        try(Connection conn = DriverManager.getConnection(da.getAddress(), da.getUsername(), da.getPassowrd())){
            try (PreparedStatement statement = conn.prepareStatement(query)){
                try(ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()) {
                        genres.add(resultSet.getString("name"));

                    }
                    cur.setGenres(genres);
                    resultSet.close();
                    statement.close();
                    Gson gson = new Gson();

                    resp.getWriter().write(gson.toJson(cur.getGenres()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
