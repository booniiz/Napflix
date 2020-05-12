
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import org.jasypt.util.password.StrongPasswordEncryptor;
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
            loginInfo.put("Recaptcha","False");
            loginInfo.put("Login","False");
            loginInfo.put("Admin","False");
            resp.getWriter().write(gson.toJson(loginInfo));
            System.out.println("Failure");
            return;
        }
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            String query ="SELECT *\n" +
                    "FROM customers\n" +
                    "WHERE email = \""+ username + "\"; ";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && new StrongPasswordEncryptor().checkPassword(password, resultSet.getString("password"))){
                HttpSession session = req.getSession();
                session.setAttribute("id",resultSet.getInt("ID"));
                session.setAttribute("firstName", resultSet.getString("firstName"));
                session.setAttribute("lastName", resultSet.getString("lastName"));
                session.setAttribute("ccID", resultSet.getString("ccID"));
                session.setAttribute("address", resultSet.getString("address"));
                session.setAttribute("type", "user");
                loginInfo.put("Recaptcha","True");
                loginInfo.put("Login","True");
                loginInfo.put("Admin","False");
                System.out.println("Login as User");
            }
            else{
                String adminQuery ="SELECT *\n" +
                        "FROM employees\n" +
                        "WHERE email = \""+ username + "\"; ";
                PreparedStatement adminStatement = conn.prepareStatement(adminQuery);
                ResultSet adminSet = adminStatement.executeQuery();
                if (adminSet.next() && new StrongPasswordEncryptor().checkPassword(password, adminSet.getString("password"))) {
                    HttpSession session = req.getSession();
                    session.setAttribute("id",0);
                    session.setAttribute("firstName", "Admin");
                    session.setAttribute("lastName", "null");
                    session.setAttribute("ccID", "null");
                    session.setAttribute("address", "null");
                    session.setAttribute("type", "admin");
                    loginInfo.put("Recaptcha","True");
                    loginInfo.put("Login","True");
                    loginInfo.put("Admin","True");
                    System.out.println("Login as Admin");
                }
                else{
                    loginInfo.put("Recaptcha","True");
                    loginInfo.put("Login","False");
                    loginInfo.put("Admin","False");
                    System.out.println("Failure");
                }
            }
            resp.getWriter().write(gson.toJson(loginInfo));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
