import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

@WebServlet("/list")
public class MovieListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String title, year,director,star,genre;
        String titlep,yearp,directorp,starp,genrep;
        int page= Integer.parseInt(req.getParameter("page"));
        int limit=Integer.parseInt(req.getParameter("limit"))+1;
        int offset=(page-1)*(limit-1);
        int rowcount=0;
        String sort = req.getParameter("sort");
        //CHECK FOR CASES
        if(req.getParameter("titleID") == null || req.getParameter("titleID") == "" ){
            title ="";
            titlep ="";
        }
        else if( req.getParameter("titleID").compareTo("*") == 0){
             title = " AND m.title regexp '^[^a-z0-9]' ";
             titlep =req.getParameter("titleID");
        }
        else if( req.getParameter("titleID").length() > 1 ){
            title = " AND m.title like '%" + req.getParameter("titleID") + "%' ";
            titlep =req.getParameter("titleID");
        }
        else{
            title = " AND m.title like '" + req.getParameter("titleID") + "%' ";
            titlep =req.getParameter("titleID");
        }

        if(req.getParameter("yearID") == null  ||req.getParameter("yearID").compareTo("") == 0){
            year ="";
            yearp ="";
        }
        else{
            year = " AND m.year = '" + req.getParameter("yearID") + "' ";
            yearp =req.getParameter("yearID");
        }

        if(req.getParameter("directorID") == null || req.getParameter("directorID").compareTo("") == 0){
            director ="";
            directorp ="";
        }
        else if( req.getParameter("directorID").length() > 1 ){
            director = " AND m.director like '%" + req.getParameter("directorID") + "%' ";
            directorp =req.getParameter("directorID");
        }
        else{
            director = " AND m.director like '" + req.getParameter("directorID") + "%' ";
            directorp =req.getParameter("directorID");
        }

        if( req.getParameter("starID") == null  ||req.getParameter("starID").compareTo("") == 0){
            star ="";
            starp ="";
        }
        else if( req.getParameter("starID").length() > 1 ){
            star = " AND s.name like '%" + req.getParameter("starID") + "%' ";
            starp =req.getParameter("starID");
        }
        else{
            star = " AND s.name like '" + req.getParameter("starID") + "%' ";
            starp =req.getParameter("starID");
        }

        if(req.getParameter("genreID") == null ||req.getParameter("genreID").compareTo("") == 0){
            genre ="";
            genrep="";
        }
        else{
            genre = " AND g.name ='" + req.getParameter("genreID") + "' ";
            genrep=req.getParameter("genreID");
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
        out.write("<nav class=\"navbar navbar-expand-lg navbar-light bg-primary\">" +
                "    <span class = \"navbar-brand text-white\">Movie List</span>\n" +
                "    <ul class=\"navbar-nav ml-auto\">\n" +
                "        <li class = \"navbar-item \">\n" +
                "            <a class = \"nav-link text-white\" href = \"/Napflix/mainmenu.html\">Back to Main Menu</a>\n" +
                "        </li>\n" +
                "        <li class = \"navbar-item\">\n" +
                "            <a class = \"nav-link text-white\" href = \"/Napflix/checkout.html\">My carts(Checkout)</a>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "</nav>");
        try{
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            //Relax MySql Standard(Set Mode to None)

            String mode = "set GLOBAL sql_mode = ''";
            PreparedStatement temp = conn.prepareStatement(mode);
            temp.executeQuery();
            temp.close();
            //Actual code
            String queryTitle = "SELECT m.id, m.title,m.year,m.director,r.rating,group_concat(DISTINCT g.name separator ',') 'genre',group_concat(DISTINCT s.name separator ',') 'star'" +
                    "FROM movies m JOIN ratings r JOIN genres g JOIN genres_in_movies gim JOIN stars_in_movies sim JOIN stars s WHERE m.id = r.movieId AND sim.starId = s.id AND sim.movieID =m.id AND m.ID = gim.movieID AND gim.genreID = g.ID " +title+year+director+star+genre+  "group by m.title order by " + sort+ " LIMIT ? OFFSET ?";
            PreparedStatement statement = conn.prepareStatement(queryTitle);
            System.out.print(queryTitle);
            statement.setInt(1, limit);
            statement.setInt(2, offset);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.last()) {
                rowcount = resultSet.getRow();
                resultSet.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            while (resultSet.next() != false){
                Movie cur = new Movie();
                cur.setId(resultSet.getString("m.id"));
                cur.setTitle(resultSet.getString("m.title"));
                cur.setYear(resultSet.getInt("m.year"));
                cur.setDirector(resultSet.getString("m.director"));
                cur.setRating(resultSet.getFloat("r.rating"));

                //Getting genre

                String genresResult = "SELECT genres.name\n" +
                        "FROM genres_in_movies\n" +
                        "INNER JOIN genres on genres_in_movies.genreID = genres.ID\n" +
                        "WHERE genres_in_movies.movieID = ? \n" +
                        "ORDER BY name ASC LIMIT 3;";
                List<String> genres = new ArrayList<>();
                PreparedStatement statement1 = conn.prepareStatement(genresResult);
                statement1.setString(1, cur.getId());
                ResultSet genresResultSet = statement1.executeQuery();
                while (genresResultSet.next() != false){
                    genres.add(genresResultSet.getString("name"));
                }
                cur.setGenres(genres);
                genresResultSet.close();
                statement1.close();

                //Getting star
                String starResult = "SELECT moviedata.name,sim.starID,count(*) FROM (SELECT name, starID FROM stars_in_movies INNER JOIN stars on stars_in_movies.starID = stars.ID WHERE stars_in_movies.movieID = ? ) AS moviedata JOIN stars_in_movies sim WHERE sim.starID = moviedata.starID GROUP BY starID ORDER BY COUNT(*) DESC, moviedata.name ASC LIMIT 3;";
                PreparedStatement statement2 = conn.prepareStatement(starResult);
                statement2.setString(1, cur.getId());
                ResultSet starResultSet = statement2.executeQuery();
                Map<String,String> stars = new LinkedHashMap<>();
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
            out.write("<div id = \"add\" class=\"alert alert-success\" role=\"alert\">\n" +
                    "        Added to cart. To remove, please go to cart.\n" +
                    "    </div>");
            out.write("<div id = \"add_error\" class=\"alert alert-danger\" role=\"alert\">\n" +
                    "        ERROR!!! \n" +
                    "    </div>");
            out.write("<table class = \"table\">");
            out.write("<span>   </span>");
            out.write("<span>   </span>");
            out.write("<label  for=\"sortie\">Sort: </label>");
            out.write("<select id=\"sortie\" onchange = \"sort();\">");
            out.write("   <option value=\"\"></option>");
            out.write("   <option value=\"m.title ASC,r.rating ASC\">title ASC,rating ASC</option>");
            out.write("   <option value=\"m.title ASC,r.rating DESC\">title ASC,rating DESC</option>");
            out.write("   <option value=\"m.title DESC,r.rating ASC\">title DESC,rating ASC</option>");
            out.write("   <option value=\"m.title DESC,r.rating DESC\">title DESC,rating DESC</option>");
            out.write("   <option value=\"r.rating  ASC,m.title ASC\">rating ASC,title ASC</option>");
            out.write("   <option value=\"r.rating  ASC,m.title DESC\">rating ASC,title DESC</option>");
            out.write("   <option value=\"r.rating  DESC,m.title ASC\">rating DESC,title ASC</option>");
            out.write("   <option value=\"r.rating  DESC,m.title DESC\">rating DESC,title DESC</option>");
            out.write(" </select>");
            out.write("</tr>");

            out.write("<tr>");
                    out.write("<th>Title</th>");
                    out.write("<th>Year</th>");
                    out.write("<th>Rating</th>");
                    out.write("<th>Genres</th>");
                    out.write("<th>Star</th>");
                    out.write("<th>add_to_cart</th>");
                out.write("</tr>");
                int size=movieList.size();
                if(size>limit-1){
                    size=movieList.size()-1;
                }
                for (int i =0;i<size;i++){

                    Movie m = movieList.get(i);
                    out.write("<tr>");
                        out.write(String.format("<th><a href = \"/Napflix/api/movie?movieID=%s&titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort=%s&page=%d&limit=%s\">%s</a></th>",
                                m.getId(),titlep,yearp,directorp,starp,genrep,req.getParameter("sort"),page,req.getParameter("limit"),m.getTitle()));
                        out.write(String.format("<th>%d</th>", m.getYear()));
                        out.write(String.format("<th>%.1f</th>", m.getRating()));
                        //genres
                        out.write("<th>");
                            out.write("<table>");
                                out.write("<tr>");
                                        for (String genrelist: m.getGenres()){
                                            out.write(String.format("<th><a href = '/Napflix/list?genreID=" +genrelist + "&sort=%s&page=%d&limit=%s'>" +genrelist+"</a></th>"
                                                    ,req.getParameter("sort"),1,req.getParameter("limit") ));
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
                                        out.write(String.format("<th><a href = \"/Napflix/api/star?starnumID=%s&titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort=%s&page=%d&limit=%s\">%s</a></th>"
                                                , starID,titlep,yearp,directorp,starp,genrep,req.getParameter("sort"),page,req.getParameter("limit"), starName));
                                    }
                                out.write("</tr> ");
                            out.write("</table>");
                        out.write("</th>");
                        out.write("<th>");
                            out.write("<button type=\"button\" class=\"btn btn-primary add_to_cart\" movieID =\""+ m.getId() +"\" title = \"" + m.getTitle() + "\">Add to cart</button>");
                        out.write("</th>");
                    out.write("</tr>");

                }
            out.write("</table>");

            out.write("<tr>");
            out.write(String.format("<span>   </span>"));
            out.write(String.format("<span>   </span>"));
            if(page !=1){
                out.write(String.format("<th><button class=\"btn btn-primary\" onclick =\"window.location.href ='/Napflix/list?titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort=%s&page=%d&limit=%s'\">Prev</button></th>",
                        titlep ,yearp,directorp,starp, genrep, req.getParameter("sort"),page-1,req.getParameter("limit")));
            }
            else{
                out.write(String.format("<th>Prev</th>"));
            }
            out.write(String.format("<span>   </span>"));
            out.write(String.format("<span>   </span>"));
            out.write(String.format("<th>Page %d</th>",page));
            out.write(String.format("<span>   </span>"));
            out.write(String.format("<span>   </span>"));
            if(rowcount >Integer.parseInt(req.getParameter("limit")) ) {
                out.write(String.format("<th><button class=\"btn btn-primary\" onclick =\"window.location.href ='/Napflix/list?titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort=%s&page=%d&limit=%s'\">Next</button></th>",
                        titlep, yearp, directorp, starp, genrep, req.getParameter("sort"), page + 1, req.getParameter("limit")));
            }
            else{
                out.write(String.format("<th>Next</th>"));
            }
            out.write(String.format("<span>   </span>"));
            out.write(String.format("<span>   </span>"));
            out.write("<label  for=\"N\">Display: </label>");
            out.write("<select id=\"N\" onchange = \"change();\">");
            out.write("   <option value=\"\"></option>");
            out.write("   <option value=\"10\">10</option>");
            out.write("   <option value=\"25\">25</option>");
            out.write("   <option value=\"50\">50</option>");
            out.write("   <option value=\"100\">100</option>");
            out.write(" </select>");
            out.write("</tr>");

            out.write("<script type=\"text/javascript\">");
            out.write("function change() {");
            out.write("var selectBox = document.getElementById(\"N\");");
            out.write("var selectedValue = selectBox.options[selectBox.selectedIndex].value;");
            out.write(String.format("window.location.replace('/Napflix/list?titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort=%s&page=%d&limit=' + selectedValue)",
                        titlep, yearp, directorp, starp, genrep, req.getParameter("sort"), 1));
            out.write(" }");
            out.write(" </script>");

            out.write("<script type=\"text/javascript\">");
            out.write("function sort() {");
            out.write("var selectBox = document.getElementById(\"sortie\");");
            out.write("var selectedValue = selectBox.options[selectBox.selectedIndex].value;");
            out.write(String.format("window.location.replace('/Napflix/list?titleID=%s&yearID=%s&directorID=%s&starID=%s&genreID=%s&sort='+selectedValue+'&page=%d&limit=%s')",
                    titlep, yearp, directorp, starp, genrep, 1,req.getParameter("limit")));
            out.write(" }");
            out.write(" </script>");
            out.write("<script src=\"https://code.jquery.com/jquery-3.4.1.min.js\"></script>\n" +
                    "<script src=\"https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js\" integrity=\"sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo\" crossorigin=\"anonymous\"></script>\n" +
                    "<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js\" integrity=\"sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6\" crossorigin=\"anonymous\"></script>");
            out.write("<script src = \"/Napflix/movielist.js\"></script>");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

