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

@WebServlet("/list")
public class MovieListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String title, year,director,star,genre;
        //CHECK EDGE CASE FOR SEARCH
        if(req.getParameter("titleID") == null || req.getParameter("titleID") == "" ){
            title ="";
        }
        else if( req.getParameter("titleID").compareTo("*") == 0){
             title = "not like 'A_Z' ";
        }
        else{
            title = " AND m.title like '" + req.getParameter("titleID") + "%' ";
        }

        if(req.getParameter("yearID") == null  ||req.getParameter("yearID").compareTo("") == 0){
            year ="";
        }
        else{
            year = " AND m.year = '" + req.getParameter("yearID") + "' ";
        }

        if(req.getParameter("directorID") == null || req.getParameter("directorID").compareTo("") == 0){
            director ="";
        }
        else{
            director = " AND m.director like '" + req.getParameter("directorID") + "%' ";
        }

        if( req.getParameter("starID") == null  ||req.getParameter("starID").compareTo("") == 0){
            star ="";
        }
        else{
            star = " AND s.name like '" + req.getParameter("starID") + "%' ";
        }

        if(req.getParameter("genreID") == null ){
            genre ="";
        }
        else{
            genre = " AND g.name ='" + req.getParameter("genreID") + "' ";
        }

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
            //Relax MySql Standard(Set Mode to None)
            Statement temp = conn.createStatement();
            temp.executeQuery("set GLOBAL sql_mode = ''");
            temp.close();
            //Actual code
            String queryTitle = "SELECT m.id, m.title,m.year,m.director,r.rating,group_concat(DISTINCT g.name separator ',') 'genre',group_concat(DISTINCT s.name separator ',') 'star'" +
                    "FROM movies m JOIN ratings r JOIN genres g JOIN genres_in_movies gim JOIN stars_in_movies sim JOIN stars s WHERE m.id = r.movieId AND sim.starId = s.id AND sim.movieID =m.id AND m.ID = gim.movieID AND gim.genreID = g.ID "
                    + title+year+director+star+genre+  "group by m.title LIMIT 30";
            PreparedStatement statement = conn.prepareStatement(queryTitle);


            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next() != false){
                Movie cur = new Movie();
                cur.setId(resultSet.getString("m.id"));
                cur.setTitle(resultSet.getString("m.title"));
                cur.setYear(resultSet.getInt("m.year"));
                cur.setDirector(resultSet.getString("m.director"));
                cur.setRating(resultSet.getFloat("r.rating"));

                //Getting genre
                Statement statement1 = conn.createStatement();
                ResultSet genresResultSet = statement1.executeQuery("SELECT genres.name\n" +
                        "FROM genres_in_movies\n" +
                        "INNER JOIN genres on genres_in_movies.genreID = genres.ID\n" +
                        "WHERE genres_in_movies.movieID = \""+cur.getId()+"\"\n" +
                        "ORDER BY name ASC LIMIT 3;");
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
            out.write(String.format("<th><a href = \"/Napflix/mainmenu.html\">BACK TO MAIN PAGE</a></th>"));
            out.write("<p></p>");
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
                        out.write(String.format("<th><a href = \"/Napflix/api/movie?movieID=%s\">%s</a></th>", m.getId(),m.getTitle()));
                        out.write(String.format("<th>%d</th>", m.getYear()));
                        out.write(String.format("<th>%.1f</th>", m.getRating()));
                        //genres
                        out.write("<th>");
                            out.write("<table>");
                                out.write("<tr>");
                                        for (String genrelist: m.getGenres()){
                                            out.write(String.format("<th><a href = '/Napflix/list?genreID=" +genrelist + "'>" +genrelist+"</a></th>"));
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
                                        out.write(String.format("<th><a href = \"/Napflix/api/star?starID=%s\">%s</a></th>", starID, starName));
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

