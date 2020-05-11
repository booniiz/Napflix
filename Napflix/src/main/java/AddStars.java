
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

import com.google.gson.Gson;

@WebServlet(name = "addStars", urlPatterns = "ajax/addStars")
public class AddStars extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("PLEASE WORKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        Gson gson = new Gson();
        String report,name;
        int maxId = 0;
        int birthday;

        if(req.getParameter("name") == null  || req.getParameter("yearID").compareTo("") == 0){
            report="FAILURE: You need to at least add name to add stars ";
            resp.getWriter().write(gson.toJson(report));
            System.out.println(report);
            return;
        }
        else{
            name = req.getParameter("name");
        }

        if(req.getParameter("birthday") == null  || req.getParameter("birthday").compareTo("") == 0){
            birthday=0;
        }
        else{
            birthday = Integer.parseInt(req.getParameter("birthday"));
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
            report = call;
            resp.getWriter().write(gson.toJson(report));
            System.out.println(report);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
