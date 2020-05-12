
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

import com.google.gson.Gson;

@WebServlet(urlPatterns = "/ajax/addStars")
public class AddStars extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        String report,name;
        int maxId = 0;
        int birthday;

        if(req.getParameter("nameID") == null  || req.getParameter("nameID").compareTo("") == 0){
            report="FAILURE: You need to at least add name to add stars ";
            resp.getWriter().write(gson.toJson(report));
            System.out.println(report);
            return;
        }
        else{
            name = req.getParameter("nameID");
        }

        if(req.getParameter("birthYearID") == null  || req.getParameter("birthYearID").compareTo("") == 0){
            birthday=0;
        }
        else{
            birthday = Integer.parseInt(req.getParameter("birthYearID"));
        }

        String query ="select max(id) 'id'from stars;";


        databaseAuthentication da = new databaseAuthentication();
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            //getting max id from stars
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                maxId = Integer.parseInt(resultSet.getString("id").substring(2));
            }
            //inserting stars
            String call =String.format("CALL add_stars('nm%d','%s',%d);",maxId+1,name,birthday);
            PreparedStatement insertStatement = conn.prepareStatement(call);
            insertStatement.executeQuery();
            report = "Success: inserted " + name +" into the database with a new ID = " +"nm"+ (maxId+1);
            resp.getWriter().write(gson.toJson(report));
            System.out.println(call);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
