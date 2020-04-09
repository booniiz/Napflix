import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/*")
public class MovieListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Napflix", "root", "mbBMyJy6UYuQ")){
            try (Statement statement = conn.createStatement()){
                try(ResultSet resultSet = statement.executeQuery(
                        "SELECT title, year, director, rating FROM movies INNER JOIN ratings on movies.ID = ratings.movieID"
                )){
                    while (resultSet.next() != false){
                        out.println(resultSet.getString("title"));
                        out.println(resultSet.getInt("year"));
                        out.println(resultSet.getString("director"));
                        out.println(resultSet.getInt("rating"));

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
