import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/*")
public class MovieListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        List<Movie> movieList = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e){
            e.printStackTrace();
        }
        // Mysql Connection and creating a Movie bean
        databaseAuthentication da = new databaseAuthentication();
        try{
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, title, year, director, rating FROM movies INNER JOIN ratings on movies.ID = ratings.movieID ORDER BY rating DESC LIMIT 20;");
            while (resultSet.next() != false){
                Movie cur = new Movie();
                cur.setId(resultSet.getString("id"));
                cur.setTitle(resultSet.getString("title"));
                cur.setYear(resultSet.getInt("year"));
                cur.setDirector(resultSet.getString("director"));
                cur.setRating(resultSet.getFloat("rating"));

                //Getting genre
                Statement statement1 = conn.createStatement();
                ResultSet genresResultSet = statement1.executeQuery("SELECT genres.name\n" +
                        "FROM genres_in_movies\n" +
                        "INNER JOIN genres on genres_in_movies.genreID = genres.ID\n" +
                        "WHERE genres_in_movies.movieID = \""+cur.getId()+"\"\n" +
                        "LIMIT 3;");
                List<String> genres = new ArrayList<>();
                while (genresResultSet.next() != false){
                    genres.add(genresResultSet.getString("name"));
                }
                cur.setGenres(genres);
                genresResultSet.close();
                statement1.close();

                //Getting star
                Statement statement2 = conn.createStatement();
                ResultSet starResultSet = statement2.executeQuery("SELECT name, starID FROM stars_in_movies INNER JOIN stars on stars_in_movies.starID = stars.ID WHERE stars_in_movies.movieID = \"" + cur.getId()  + "\"LIMIT 3;");
                Map<String,String> stars = new HashMap<>();
                while (starResultSet.next() != false){
                    stars.put(starResultSet.getString("starID"), starResultSet.getString("name"));
                }
                cur.setStarIDMap(stars);
                starResultSet.close();
                statement2.close();

                movieList.add(cur);

            }
            resultSet.close();
            statement.close();
            conn.close();
            // Creating HTML
            out.write("<table>");
                out.write("<tr>");
                    out.write("<th>Title</th>");
                    out.write("<th>Year</th>>");
                    out.write("<th>Rating</th>");
                    out.write("<th>Genres</th>");
                    out.write("<th>Star</th>");
                out.write("</tr>");
                for (Movie m: movieList){
                    out.write("<tr>");
                        out.write(String.format("<th><a href = \"/Napflix_war/api/movie?movieID=%s\">%s</a></th>", m.getId(),m.getTitle()));
                        out.write(String.format("<th>%d</th>", m.getYear()));
                        out.write(String.format("<th>%.1f</th>", m.getRating()));
                        //genres
                        out.write("<th>");
                            out.write("<table>");
                                out.write("<tr>");
                                        for (String genre: m.getGenres()){
                                            out.write(String.format("<th>%s</th>", genre));
                                        }
                                out.write("</tr> ");
                            out.write("</table>");
                        out.write("</th>");

                        //stars
                        out.write("<th>");
                            out.write("<table>");
                                out.write("<tr>");
                                    for (Map.Entry<String, String> entry: m.getStarIDMap().entrySet()){
                                        String starID = entry.getKey();
                                        String starName = entry.getValue();
                                        out.write(String.format("<th><a href = \"/Napflix_war/api/star?starID=%s\">%s</a></th>", starID, starName));
                                    }
                                out.write("</tr> ");
                            out.write("</table>");
                        out.write("</th>");
                    out.write("</tr>");
                }
            out.write("</table>");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

