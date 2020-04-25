import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();
    private float total = 0;

    public Cart() {
    }

    public Cart(List<CartItem> items) {
        this.items = items;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public void addItem(CartItem item){
        items.add(item);
        this.total += item.getPrice() * item.getQuantity();
        if (this.total < 0){
            this.total = 0;
        }
    }

    public void calculateTotal(){
        float total = 0;
        for (CartItem cartItem : this.getItems()){
            total += cartItem.getPrice() * cartItem.getQuantity();
        }
        this.total = total;
    }
}
