import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(urlPatterns = "/ajax/Androidlogin")
/**
 * This class is based on our project 2's user authentication mechanism(219c812), it does not require recaptcha and does not allow admin access.
 */
public class AndroidUserAuth extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        databaseAuthentication da = new databaseAuthentication();
        JsonObject respJson = new JsonObject();
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT *\n" +
                    "FROM customers\n" +
                    "WHERE email = \""+ username + "\"; ");
            if (resultSet.next() && new StrongPasswordEncryptor().checkPassword(password, resultSet.getString("password"))){
                System.out.println("Sucuess");
                HttpSession session = req.getSession();
                session.setAttribute("id",resultSet.getInt("ID"));
                session.setAttribute("firstName", resultSet.getString("firstName"));
                session.setAttribute("lastName", resultSet.getString("lastName"));
                session.setAttribute("ccID", resultSet.getString("ccID"));
                session.setAttribute("address", resultSet.getString("address"));
                respJson.addProperty("Login", "True");
            }else{
                respJson.addProperty("Login", "False");
                System.out.println("Failure");
            }
            resp.getWriter().write(respJson.toString());

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}