import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
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
        Boolean change_mode = Boolean.valueOf(req.getParameter("change_mode"));
        //Debug code
        System.out.println(session.getId() + " Adding Item");
        System.out.println("movieID " + mID);
        System.out.println("title" + mID);
        System.out.println("quantity " + quantity);
        System.out.println("price" + price);
        System.out.println("change_mode" + change_mode);
        //End Debugcode
        Gson gson = new Gson();
        Cart cart;
        if (session.getAttribute("cart") == null){
            cart = new Cart();
        }else{
            cart = (Cart) session.getAttribute("cart");
        }

        for (CartItem cartItem : cart.getItems()){
            if (cartItem.getMovieID().equals(req.getParameter("movieID"))){
                Integer newQuantity;
                if (change_mode){
                    newQuantity = new Integer(req.getParameter("quantity"));
                }else{
                    newQuantity = cartItem.getQuantity() + new Integer(req.getParameter("quantity"));
                }
                if (newQuantity <= 0){
                    cart.getItems().remove(cartItem);
                    cart.calculateTotal();
                }else{
                    cartItem.setQuantity(newQuantity);
                }
                cart.calculateTotal();
                session.setAttribute("cart", cart);
                System.out.println(gson.toJson(cart));
                return;
            }
            if (cart.getItems().size() == 0){
                session.removeAttribute("cart");
            }
        }
        //Create new item if quantity is bigger than zero
        if (new Integer(quantity) > 0){
            cart.addItem(new CartItem(mID, title, new Integer(quantity), new Float(price)));
            System.out.println(cart.getItems().toArray().toString());
            System.out.println("Json for cart: " + gson.toJson(cart));
        }
        cart.calculateTotal();
        session.setAttribute("cart", cart);
        System.out.println(gson.toJson(cart));
    }
}
