public class CartItem {
    private String movieID;
    private int quanitty ;
    private float price = 1;
    private String title;

    public CartItem(String movieID, int quanitty, float price) {
        this.movieID = movieID;
        this.quanitty = quanitty;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CartItem(String mID, String title, Integer integer, Float aFloat) {
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public int getQuanitty() {
        return quanitty;
    }

    public void setQuanitty(int quanitty) {
        this.quanitty = quanitty;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
