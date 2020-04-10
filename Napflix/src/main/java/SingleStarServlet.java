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


@WebServlet(name = "StarServlet", urlPatterns = "/api/star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
    @Resource(name = "jdbc:mysql://localhost:3306/122B")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{

        String starID = req.getParameter("starID");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String query = "SELECT s.name,s.birthYear, GROUP_CONCAT(DISTINCT m.title) 'movies'" +
                "FROM stars s inner join stars_in_movies sim inner join movies m " +
                "WHERE s.id = sim.starID AND sim.movieID = m.id AND s.id = ?";

        try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/122B", "root", "5B2b43d5b3?")){
            try (PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1, starID);
                try(ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()) {
                        out.println("Name: " + resultSet.getString("s.name") + "\n");
                        out.println("Year of Birth: " +resultSet.getInt("s.birthYear") + "\n");
                        out.println("Movies: " +resultSet.getString("movies") + "\n");
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
