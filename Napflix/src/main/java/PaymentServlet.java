import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;

@WebServlet(urlPatterns = "/ajax/pay")
public class PaymentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
        HttpSession session = req.getSession();
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String cc = req.getParameter("cc");
        String expiration = req.getParameter("expiration");
        Cart cart = (Cart) session.getAttribute("cart");
        JsonObject respJson = new JsonObject();
        try{
            databaseAuthentication da = new databaseAuthentication();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            Statement statement = conn.createStatement();
            PreparedStatement checkIfCardIsValid = conn.prepareStatement("SELECT *\n" +
                    "FROM creditcards\n" +
                    "WHERE ID = ? and firstName = ? and lastName = ? and expiration = ?;");
            checkIfCardIsValid.setString(1,cc);
            checkIfCardIsValid.setString(2,firstName);
            checkIfCardIsValid.setString(3,lastName);
            checkIfCardIsValid.setString(4,expiration);
            ResultSet resultSet = checkIfCardIsValid.executeQuery();
            if (resultSet.next() == true){
                statement.close();
                resultSet.close();
                respJson.addProperty("CCValid","True");
            }else{
                respJson.addProperty("CCValid", "False");
            }
            resp.getWriter().write(respJson.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
