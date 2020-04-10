import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet(name = "MovieServlet", urlPatterns = "/api/movie")
public class MovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
    @Resource(name = "jdbc:mysql://localhost:3306/122B")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{

        String movieID = req.getParameter("movieID");
        resp.setContentType("application/json");
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
                        out.println("Title: " + resultSet.getString("m.title") + "\n");
                        out.println("Year: " +resultSet.getInt("m.year") + "\n");
                        out.println("Director: " +resultSet.getString("m.director") + "\n");
                        out.println("Genres: " +resultSet.getString("genres") + "\n");
                        out.println("Stars: " +resultSet.getString("stars") + "\n");
                        out.println("Rating: " +resultSet.getString("r.rating"));
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
