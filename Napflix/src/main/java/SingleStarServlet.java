import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet(name = "StarServlet", urlPatterns = "/api/star")
public class SingleStarServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{

        String starID = req.getParameter("starID");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String query = "SELECT s.name,s.birthYear, GROUP_CONCAT(DISTINCT m.id) 'movies'" +
                "FROM stars s inner join stars_in_movies sim inner join movies m " +
                "WHERE s.id = sim.starID AND sim.movieID = m.id AND s.id = ?";

        String query2 = "SELECT s.title FROM movies s WHERE s.id = ?";

        databaseAuthentication da = new databaseAuthentication();
        try(Connection conn = DriverManager.getConnection(da.getAddress(), da.getUsername(), da.getPassowrd())){
            try (PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1, starID);
                try(ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()) {
                        out.write(String.format("<th><a href = \"/Napflix\">BACK TO MAIN PAGE</a></th>"));
                        String[] tokens = resultSet.getString("movies").split(",");
                        out.write("<p>Name: " + resultSet.getString("s.name") + "</p>");
                        int birthYear = resultSet.getInt("s.birthYear");
                        if (birthYear != 0){
                            out.write("<p>Year of Birth: " + birthYear + "</p>");
                        }else{
                            out.write("<p>Year of Birth: (Not Available)</p>");
                        }
                        out.write("<p>Movies: ");
                        for (String t : tokens) {
                            PreparedStatement statement2 = conn.prepareStatement(query2);
                            statement2.setString(1, t);
                            ResultSet resultSet2 = statement2.executeQuery();
                            while (resultSet2.next()) {
                                out.write(String.format("<th><a href = \"/Napflix/api/movie?movieID=%s\">%s</a></th>", t, resultSet2.getString("s.title")) + ", ");
                            }
                        }
                        out.write("</p>");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
