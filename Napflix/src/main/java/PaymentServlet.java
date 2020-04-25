import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.Calendar;

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
                PreparedStatement insertIntoTable = conn.prepareStatement("INSERT INTO sales(customerID, movieID, saleDate) VALUES (?,?,?)");
                long current_time = Calendar.getInstance().getTime().getTime();
                for (CartItem cartItem: cart.getItems()){
                    insertIntoTable.setInt(1, (int) session.getAttribute("id"));
                    insertIntoTable.setString(2,cartItem.getMovieID());
                    insertIntoTable.setDate(3, new java.sql.Date(current_time));
                    insertIntoTable.executeUpdate();
                }
                session.removeAttribute("cart");

            }else{
                respJson.addProperty("CCValid", "False");
            }
            resp.getWriter().write(respJson.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
