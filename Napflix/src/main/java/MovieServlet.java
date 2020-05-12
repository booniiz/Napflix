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
        String titlep = req.getParameter("titleID");
        String yearp = req.getParameter("yearID");
        String directorp = req.getParameter("directorID");
        String starp = req.getParameter("starID");
        String genrep = req.getParameter("genreID");
        String sortp = req.getParameter("sort");
        String pagep = req.getParameter("page");
        String limitp = req.getParameter("limit");
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

        String query2 = "SELECT moviedata.name,sim.starID,count(*) FROM (SELECT name, starID FROM stars_in_movies INNER JOIN stars on stars_in_movies.starID = stars.ID WHERE stars_in_movies.movieID = ? ) AS moviedata JOIN stars_in_movies sim WHERE sim.starID = moviedata.starID GROUP BY starID ORDER BY COUNT(*) DESC, moviedata.name ASC LIMIT 3";

        databaseAuthentication da = new databaseAuthentication();
        out.write("<head>\n" +
                "    <!-- Required meta tags -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "\n" +
                "    <!-- Bootstrap CSS -->\n" +
                "    <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\" integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">\n" +
                "\n" +
                "\n" +
                "</head>");
        out.write("<div id = \"add\" class=\"alert alert-success\" role=\"alert\">\n" +
                "        Added to cart. To remove, please go to cart.\n" +
                "    </div>");
        out.write("<div id = \"add_error\" class=\"alert alert-danger\" role=\"alert\">\n" +
                "        ERROR!!! \n" +
                "    </div>");
        try(Connection conn = DriverManager.getConnection(da.getAddress(), da.getUsername(), da.getPassowrd())){
            try (PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1, movieID);
                try(ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()) {
                        String s = String.format("<a class = \"nav-link text-white\" href = \"/Napflix/list?titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort=%s&page=%s&limit=%s\">BACK TO MOVIE LIST</a>"
                                ,titlep,yearp,directorp,starp,genrep,sortp,pagep,limitp);
                        out.write("<nav class=\"navbar navbar-expand-lg navbar-light bg-primary\">\n" +
                                "    <span class = \" navbar-brand text-white \">Movie</span>\n" +
                                "    <ul class=\"navbar-nav ml-auto\">\n" +
                                "        <li class = \"navbar-item\">\n" +
                                "            <a class = \"nav-link text-white\" href = \"/Napflix/checkout.html\">My carts(Checkout)</a>\n" +
                                "        </li>\n" +
                                "        <li class = \"navbar-item\">\n" +
                                            s +
                                "        </li>\n" +
                                "    </ul>\n" +
                                "</nav>");
                        String[] token1 = resultSet.getString("genres").split(",");
                        String title = resultSet.getString("m.title");
                        out.write("<p>Title: " + title + "</p>");
                        out.write("<p>Year: " +resultSet.getInt("m.year") + "</p>");
                        out.write("<p>Director: " +resultSet.getString("m.director") + "</p>");
                        out.write("<p>Genres: ");
                        for (String t1 : token1) {
                            out.write(String.format("<th><a href = \"/Napflix/list?genreID=%s&sort=%s&page=%d&limit=%s\">%s</a></th>", t1,sortp,1,limitp, t1) + " ");
                        }
                        out.write("</p>");
                        out.write("<p>Stars: ");
                        PreparedStatement statement2 = conn.prepareStatement(query2);
                        statement2.setString(1, movieID);
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
                            out.write(String.format("<th><a href = \"/Napflix/api/star?starnumID=%s&titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort=%s&page=%s&limit=%s\">%s</a></th>"
                                    ,starID, titlep,yearp,directorp,starp,genrep,sortp,pagep,limitp, starName)+ " ");
                        }
                        out.write("</p>");
                        out.write("<p>Rating: " +resultSet.getString("r.rating")+"</p>");

                        out.write("<script src=\"https://code.jquery.com/jquery-3.4.1.min.js\"></script>\n" +
                                "<script src=\"https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js\" integrity=\"sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo\" crossorigin=\"anonymous\"></script>\n" +
                                "<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js\" integrity=\"sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6\" crossorigin=\"anonymous\"></script>");
                        out.write("<script src = \"/Napflix/movielist.js\"></script>");
                        out.write("<button type=\"button\" class=\"btn btn-primary add_to_cart\" movieID = \" "+movieID+"\" title = \"" + title + "\">Add to cart</button>");

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
