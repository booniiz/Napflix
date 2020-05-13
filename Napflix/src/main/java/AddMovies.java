
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

import com.google.gson.Gson;

@WebServlet(urlPatterns = "/ajax/addMovies")
public class AddMovies extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        String report,title,director,star,genre;
        String movieID,genreID,starID;
        int year,birthday,genreFlag=0,starFlag=0;

        //Avoid empty parameters
        if(req.getParameter("titleID") == null  || req.getParameter("titleID").compareTo("") == 0){
            report="FAILURE: Name Unknown ";
            resp.getWriter().write(gson.toJson(report));
            System.out.println(report);
            return;
        }
        else{
            title = req.getParameter("titleID");
        }
        if(req.getParameter("yearID") == null  || req.getParameter("yearID").compareTo("") == 0){
            report="FAILURE: Year Unknown ";
            resp.getWriter().write(gson.toJson(report));
            System.out.println(report);
            return;
        }
        else{
            year = Integer.parseInt(req.getParameter("yearID"));

        }
        if(req.getParameter("directorID") == null  || req.getParameter("directorID").compareTo("") == 0){
            report="FAILURE: Director Unknown ";
            resp.getWriter().write(gson.toJson(report));
            System.out.println(report);
            return;
        }
        else{
            director = req.getParameter("directorID");
        }
        if(req.getParameter("starID") == null  || req.getParameter("starID").compareTo("") == 0){
            report="FAILURE: Star Unknown";
            resp.getWriter().write(gson.toJson(report));
            System.out.println(report);
            return;
        }
        else{
            star = req.getParameter("starID");
        }
        if(req.getParameter("genreID") == null  || req.getParameter("genreID").compareTo("") == 0){
            report="FAILURE: Genre Unknown ";
            resp.getWriter().write(gson.toJson(report));
            System.out.println(report);
            return;
        }
        else{
            genre = req.getParameter("genreID");
        }
        if(req.getParameter("birthdayID") == null  || req.getParameter("birthdayID").compareTo("") == 0){
            birthday=-1;
        }
        else{
            birthday = Integer.parseInt(req.getParameter("birthdayID"));
        }

        databaseAuthentication da = new databaseAuthentication();
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            //-------MAXID------------------------------------------------------------------------------------
            String maxQuery = "select max(s.id) 'sid',max(m.id) 'mid', max(g.id) 'gid' from stars as s JOIN movies as m JOIN genres as g;";
            PreparedStatement getMax = conn.prepareStatement(maxQuery);
            ResultSet idSet = getMax.executeQuery();
            if (idSet.next()){
                movieID = idSet.getString("mid");
                genreID = idSet.getString("gid");
                starID = idSet.getString("sid");
            }
            else{
                System.out.println("ERROR: MAX NOT FOUND");
                return;
            }
            //-------MAXID------------------------------------------------------------------------------------

            //-------MOVIE------------------------------------------------------------------------------------
            String checkMovie = "Select * from movies where title = ? AND year = ? AND director = ? LIMIT 1;";
            PreparedStatement getMovie = conn.prepareStatement(checkMovie);
            getMovie.setString(1, title);
            getMovie.setString(2, Integer.toString(year));
            getMovie.setString(3, director);
            ResultSet movieSet = getMovie.executeQuery();
            if (movieSet.next()){
                System.out.print("MOVIE MATCH FOUND:"+movieSet.getString("id"));
                report = "Failure: Duplicate Movie";
                resp.getWriter().write(gson.toJson(report));
                return;
            }
            else{
                try{
                    movieID = "tt"+(Integer.parseInt(movieID.substring(2))+1);
                }catch (Exception e){
                    try{
                        movieID = movieID.substring(0,3)+(Integer.parseInt(movieID.substring(3))+1);
                    }catch (Exception ex){
                        movieID = movieID + "1";
                    }
                }
                report = "Success: Inserted movieID = " + movieID;
            }
            //-------MOVIE--------------------------------------------------------------------------------------
            //-------Star------------------------------------------------------------------------------------
            PreparedStatement getStar;
            if(birthday != -1) {
                String checkStar = "select * from stars where name = ? AND birthyear = ? LIMIT 1;";
                getStar = conn.prepareStatement(checkStar);
                getStar.setString(1, star);
                getStar.setString(2, Integer.toString(birthday));

            }
            else{
                String checkStar = "select * from stars where name = ? LIMIT 1";
                getStar = conn.prepareStatement(checkStar);
                getStar.setString(1, star);
            }
            ResultSet starSet = getStar.executeQuery();
            if (starSet.next()){
                System.out.print("Star MATCH FOUND:" + starSet.getString("id"));
                starID=starSet.getString("id");
                starFlag =1;
                report = report + ", starID(existing) = " + starID;
            }
            else{
                starID = "nm"+(Integer.parseInt(starID.substring(2))+1);
                report = report + ", starID(new) = " + starID;
            }
            //-------Star--------------------------------------------------------------------------------------
            //-------Genre------------------------------------------------------------------------------------
            String checkGenre = "select * from genres where name = ? LIMIT 1;";
            PreparedStatement getGenre = conn.prepareStatement(checkGenre);
            getGenre.setString(1, genre);
            ResultSet genreSet = getGenre.executeQuery();

            if (genreSet.next()){
                System.out.print("GENRE MATCH FOUND:"+genreSet.getString("id"));
                genreID=genreSet.getString("id");
                genreFlag=1;
                report = report + ", genreID(existing) = " + genreID;
            }
            else{
                genreID = Integer.toString(Integer.parseInt(genreID)+1);
                report = report + ", genreID(new) = " + genreID;
            }
            //-------Genre--------------------------------------------------------------------------------------

            String call ="Call add_movie(?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement insertStatement = conn.prepareStatement(call);
            insertStatement.setString(1, movieID);
            insertStatement.setString(2, title);
            insertStatement.setInt(3, year);
            insertStatement.setString(4, director);
            insertStatement.setString(5, starID);
            insertStatement.setString(6, star);
            insertStatement.setInt(7, birthday);
            insertStatement.setInt(8, Integer.parseInt(genreID));
            insertStatement.setString(9, genre);
            insertStatement.setInt(10, genreFlag);
            insertStatement.setInt(11, starFlag);
            ResultSet callSet =insertStatement.executeQuery();
            report = report+"!!";
            resp.getWriter().write(gson.toJson(report));
            System.out.println(call);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
