import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = "/ajax/addtocart")
public class AddToCart extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String mID = req.getParameter("movieID");
        String title = req.getParameter("title");
        String quantity = req.getParameter("quantity");
        String price = req.getParameter("price");
        System.out.println("movieID " + mID);
        System.out.println("title" + mID);
        System.out.println("quantity " + quantity);
        System.out.println("price" + price);
        Gson gson = new Gson();
        Cart cart;
        if (session.getAttribute("cart") == null){
            cart = new Cart();
        }else{
            cart = (Cart) session.getAttribute("cart");
        }
        cart.addItem(new CartItem(mID, title, new Integer(quantity), new Float(price)));
        System.out.println(cart.getItems().toArray().toString());
        session.setAttribute("cart", cart);
        System.out.println("Json for cart: " + gson.toJson(cart));
    }
}
