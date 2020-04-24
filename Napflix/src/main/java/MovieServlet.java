import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(name = "MovieServlet", urlPatterns = "/api/movie")
public class MovieServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{

        String movieID = req.getParameter("movieID");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * The following code query can't run when sql_mode = only_full_group_by
         * How to fix it?
         * Run SET GLOBAL sql_mode = '' in console to make it relax its requirement
         */
        String query = "SELECT m.title,m.year,m.director, " +
                "GROUP_CONCAT(DISTINCT g.name) 'genres',GROUP_CONCAT(DISTINCT s.id) 'stars', r.rating" +
                " FROM movies m INNER JOIN ratings r INNER JOIN genres_in_movies gin INNER JOIN genres g" +
                " INNER JOIN stars_in_movies sim INNER JOIN stars s WHERE m.id = r.movieId " +
                "AND sim.starId = s.id AND sim.movieID =m.id AND g.id = gin.genreId " +
                "AND gin.movieId = m.id AND m.id =?";

        String query2 = "SELECT moviedata.name,sim.starID,count(*) FROM 	(SELECT name, starID FROM stars_in_movies INNER JOIN stars on stars_in_movies.starID = stars.ID WHERE stars_in_movies.movieID = \"" + movieID + "\") AS moviedata JOIN stars_in_movies sim WHERE sim.starID = moviedata.starID GROUP BY starID ORDER BY COUNT(*) DESC, moviedata.name ASC LIMIT 3";

        databaseAuthentication da = new databaseAuthentication();
        try(Connection conn = DriverManager.getConnection(da.getAddress(), da.getUsername(), da.getPassowrd())){
            try (PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1, movieID);
                try(ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()) {
                        out.write(String.format("<th><a href = \"/Napflix/mainmenu.html\">BACK TO MAIN PAGE</a></th>"));
                        String[] tokens = resultSet.getString("stars").split(",");
                        String[] token1 = resultSet.getString("genres").split(",");
                        out.write("<p>Title: " + resultSet.getString("m.title") + "</p>");
                        out.write("<p>Year: " +resultSet.getInt("m.year") + "</p>");
                        out.write("<p>Director: " +resultSet.getString("m.director") + "</p>");
                        out.write("<p>Genres: ");
                        for (String t1 : token1) {
                            out.write(String.format("<th><a href = \"/Napflix/list?genreID=%s\">%s</a></th>", t1, t1) + " ");
                        }
                        out.write("</p>");
                        out.write("<p>Stars: ");
                        PreparedStatement statement2 = conn.prepareStatement(query2);
                        ResultSet resultSet2 = statement2.executeQuery();
                        //TOKENIZE each starID and store in an array
                        Map<String,String> stars = new LinkedHashMap<>();
                        while (resultSet2.next() != false){
                            stars.put(resultSet2.getString("starID"), resultSet2.getString("name"));
                        }
                        Movie cur = new Movie();
                        cur.setStarIDMap(stars);
                        for (Map.Entry<String, String> entry: cur.getStarIDMap().entrySet()){
                            String starID = entry.getKey();
                            String starName = entry.getValue();
                            out.write(String.format("<th><a href = \"/Napflix/api/star?starID=%s\">%s</a></th>", starID, starName)+ " ");
                        }
                        out.write("</p>");
                        out.write("<p>Rating: " +resultSet.getString("r.rating")+"</p>");
                    }
                    resultSet.close();
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
