
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import com.google.gson.Gson;

@WebServlet(urlPatterns = "/ajax/login")
public class UserAuth extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String gRecaptchaResponse = req.getParameter("recaptcha");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        databaseAuthentication da = new databaseAuthentication();
        HashMap<String, String> loginInfo = new HashMap<String, String>();
        Gson gson = new Gson();

        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            System.out.println(e);
            loginInfo.put("Recaptcha","False");
            loginInfo.put("Login","False");
            resp.getWriter().write(gson.toJson(loginInfo));
            return;
        }
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT *\n" +
                    "FROM customers\n" +
                    "WHERE email = \""+ username +"\" and password = \""+ password +"\";");
            if (resultSet.next()){
                System.out.println("Success");
                HttpSession session = req.getSession();
                session.setAttribute("id",resultSet.getInt("ID"));
                session.setAttribute("firstName", resultSet.getString("firstName"));
                session.setAttribute("lastName", resultSet.getString("lastName"));
                session.setAttribute("ccID", resultSet.getString("ccID"));
                session.setAttribute("address", resultSet.getString("address"));
                loginInfo.put("Recaptcha","True");
                loginInfo.put("Login","True");
            }
            else{
                loginInfo.put("Recaptcha","True");
                loginInfo.put("Login","False");
                System.out.println("Failure");
            }
            resp.getWriter().write(gson.toJson(loginInfo));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
