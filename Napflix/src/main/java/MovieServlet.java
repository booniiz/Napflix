import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet(name = "MovieServlet", urlPatterns = "/api/movie")
public class MovieServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{

        String movieID = req.getParameter("movieID");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String query = "SELECT m.title,m.year,m.director, " +
                "GROUP_CONCAT(DISTINCT g.name) 'genres',GROUP_CONCAT(DISTINCT s.name) 'stars', r.rating" +
                " FROM movies m INNER JOIN ratings r INNER JOIN genres_in_movies gin INNER JOIN genres g" +
                " INNER JOIN stars_in_movies sim INNER JOIN stars s WHERE m.id = r.movieId " +
                "AND sim.starId = s.id AND sim.movieID =m.id AND g.id = gin.genreId " +
                "AND gin.movieId = m.id AND m.id = ?";

        try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/122B", "root", "5B2b43d5b3?")){
            try (PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1, movieID);
                try(ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()) {
                        out.write("<p>Title: " + resultSet.getString("m.title") + "</p>");
                        out.write("<p>Year: " +resultSet.getInt("m.year") + "</p>");
                        out.write("<p>Director: " +resultSet.getString("m.director") + "</p>");
                        out.write("<p>Genres: " +resultSet.getString("genres") + "</p>");
                        out.write("<p>Stars: " +resultSet.getString("stars") + "</p>");
                        out.write("<p>Rating: " +resultSet.getString("r.rating")+"</p>");
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
