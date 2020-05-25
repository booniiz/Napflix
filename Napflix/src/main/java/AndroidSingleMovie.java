import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/AndroidSingleMovie")
public class AndroidSingleMovie extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{

        String movieID = req.getParameter("movieID");
        String titlep = req.getParameter("titleID");
        String yearp = req.getParameter("yearID");
        String directorp = req.getParameter("directorID");
        String starp = req.getParameter("starID");
        String genrep = req.getParameter("genreID");
        String sortp = req.getParameter("sort");
        String pagep = req.getParameter("page");
        String limitp = req.getParameter("limit");
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

        String query2 = "SELECT moviedata.name,sim.starID,count(*) FROM (SELECT name, starID FROM stars_in_movies INNER JOIN stars on stars_in_movies.starID = stars.ID WHERE stars_in_movies.movieID = \"" + movieID + "\") AS moviedata JOIN stars_in_movies sim WHERE sim.starID = moviedata.starID GROUP BY starID ORDER BY COUNT(*) DESC, moviedata.name ASC LIMIT 3";

        databaseAuthentication da = new databaseAuthentication();
        try(Connection conn = DriverManager.getConnection(da.getAddress(), da.getUsername(), da.getPassowrd())){
            try (PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1, movieID);
                try(ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()) {
                        String[] genres = resultSet.getString("genres").split(",");
                        String title = resultSet.getString("m.title");
                        int year = resultSet.getInt("m.year");
                        String director = resultSet.getString("m.director");
                        PreparedStatement statement2 = conn.prepareStatement(query2);
                        ResultSet resultSet2 = statement2.executeQuery();
                        //TOKENIZE each starID and store in an array
                        Map<String,String> stars = new LinkedHashMap<>();
                        while (resultSet2.next()){
                            stars.put(resultSet2.getString("starID"), resultSet2.getString("name"));
                        }
                        Movie cur = new Movie();
                        cur.setStarIDMap(stars);
                        cur.setDirector(director);
                        cur.setYear(year);
                        cur.setGenres(Arrays.asList(genres));
                        cur.setTitle(title);
                        Gson gson = new Gson();
                        out.write(gson.toJson(cur));

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
