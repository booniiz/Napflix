public class CartItem {
    private String movieID;
    private int quantity;
    private float price = 1;
    private String title;

    public CartItem() {
    }

    public CartItem(String mID, String title, Integer quantity, Float price) {
        this.movieID = mID;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
