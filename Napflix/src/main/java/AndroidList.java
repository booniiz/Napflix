import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@WebServlet("/androidList")
public class AndroidList extends HttpServlet {
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
            //FULL TEXT SEARCH
            String[] tokens = req.getParameter("titleID").split(" ");
            String token = "";
            for (String t : tokens) {
                if(t.length() > 3) {
                    token = token + "+" + t + "*% ";
                }
                else{
                    token = token + t + "*% ";
                }
            }
            title = " AND MATCH (m.title) AGAINST ('" + token + "' IN BOOLEAN MODE) ";
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
        try{
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            //Relax MySql Standard(Set Mode to None)

            String mode = "set GLOBAL sql_mode = ''";
            PreparedStatement temp = conn.prepareStatement(mode);
            temp.executeQuery();
            temp.close();
            //Actual code
            String queryTitle = "SELECT m.id, m.title,m.year,m.director,r.rating,group_concat(DISTINCT g.name separator ',') 'genre',group_concat(DISTINCT s.name separator ',') 'star'" +
                    "FROM movies m JOIN ratings r JOIN genres g JOIN genres_in_movies gim JOIN stars_in_movies sim JOIN stars s WHERE m.id = r.movieId AND sim.starId = s.id AND sim.movieID =m.id AND m.ID = gim.movieID AND gim.genreID = g.ID "
                    + title+year+director+star+genre+  "group by m.title order by " + sort+ " LIMIT ? OFFSET ?";
            PreparedStatement statement = conn.prepareStatement(queryTitle);
            System.out.println(queryTitle);
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            System.out.println(statement.toString());
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
                        "WHERE genres_in_movies.movieID = \""+cur.getId()+"\"\n" +
                        "ORDER BY name ASC LIMIT 3;";
                List<String> genres = new ArrayList<>();
                PreparedStatement statement1 = conn.prepareStatement(genresResult);
                ResultSet genresResultSet = statement1.executeQuery();
                while (genresResultSet.next() != false){
                    genres.add(genresResultSet.getString("name"));
                }
                cur.setGenres(genres);
                genresResultSet.close();
                statement1.close();

                //Getting star
                String starResult = "SELECT moviedata.name,sim.starID,count(*) FROM 	(SELECT name, starID FROM stars_in_movies INNER JOIN stars on stars_in_movies.starID = stars.ID WHERE stars_in_movies.movieID = \"" + cur.getId()  + "\") AS moviedata JOIN stars_in_movies sim WHERE sim.starID = moviedata.starID GROUP BY starID ORDER BY COUNT(*) DESC, moviedata.name ASC LIMIT 3;";
                PreparedStatement statement2 = conn.prepareStatement(starResult);
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
            Gson gson = new Gson();
            resp.getWriter().write(gson.toJson(movieList));
            // Creating HTML
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

