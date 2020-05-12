import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;


@WebServlet(name = "StarServlet", urlPatterns = "/api/star")
public class SingleStarServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{

        String titlep = req.getParameter("titleID");
        String yearp = req.getParameter("yearID");
        String directorp = req.getParameter("directorID");
        String starp = req.getParameter("starID");
        String genrep = req.getParameter("genreID");
        String sortp = req.getParameter("sort");
        String pagep = req.getParameter("page");
        String limitp = req.getParameter("limit");
        String starID = req.getParameter("starnumID");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String query = "SELECT s.name,s.birthYear, GROUP_CONCAT(DISTINCT m.id) 'movies'" +
                " FROM stars s inner join stars_in_movies sim inner join movies m " +
                " WHERE s.id = sim.starID AND sim.movieID = m.id AND s.id = ?";
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


        databaseAuthentication da = new databaseAuthentication();
        try(Connection conn = DriverManager.getConnection(da.getAddress(), da.getUsername(), da.getPassowrd())){
            try (PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1, starID);
                try(ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()) {
                        String s = String.format("<th><a class = \"nav-link text-white\"href = \"/Napflix/list?titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort=%s&page=%s&limit=%s\">BACK TO MOVIE LIST</a></th>"
                                ,titlep,yearp,directorp,starp,genrep,sortp,pagep,limitp);
                        out.write("<nav class=\"navbar navbar-expand-lg navbar-light bg-primary\">" +
                                "    <span class = \"navbar-brand text-white\">Movie List</span>\n" +
                                "    <ul class=\"navbar-nav ml-auto\">\n" +
                                "        <li class = \"navbar-item \">\n" +
                                s+
                                "        </li>\n" +
                                "        <li class = \"navbar-item\">\n" +
                                "            <a class = \"nav-link text-white\" href = \"/Napflix/checkout.html\">My carts(Checkout)</a>\n" +
                                "        </li>\n" +
                                "    </ul>\n" +
                                "</nav>");
                        out.write("<p>Name: " + resultSet.getString("s.name") + "</p>");
                        int birthYear = resultSet.getInt("s.birthYear");
                        if (birthYear != 0){
                            out.write("<p>Year of Birth: " + birthYear + "</p>");
                        }else{
                            out.write("<p>Year of Birth: (Not Available)</p>");
                        }
                        out.write("<p>Movies: ");
                        String[] tokens = resultSet.getString("movies").split(",");
                        String movielist="";
                        int i =0;
                        for (String t : tokens){
                            if(i==0) {
                                movielist = "'" + t + "'";
                                i =1;
                            }
                            else{
                                movielist = movielist + ",'" + t + "'";
                            }
                        }
                        String query2 = "SELECT s.title,s.id FROM movies s WHERE s.id in ("+movielist+") order by cast(`year` as signed ) desc, s.title ASC";
                        PreparedStatement statement2 = conn.prepareStatement(query2);
                        ResultSet resultSet2 = statement2.executeQuery();
                        Map<String,String> movies = new LinkedHashMap<>();
                        while (resultSet2.next() != false){
                            movies.put(resultSet2.getString("s.title"), resultSet2.getString("s.id"));
                        }

                        for (Map.Entry<String, String> entry: movies.entrySet()){
                            String name = entry.getKey();
                            String id = entry.getValue();
                            out.write(String.format("<th><a href = \"/Napflix/api/movie?movieID=%s&titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort=%s&page=%s&limit=%s\">%s</a></th>",
                                    id,titlep,yearp,directorp,starp,genrep,sortp,pagep,limitp,name)+ " ");
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
