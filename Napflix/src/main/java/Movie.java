import java.util.List;
import java.util.Map;

public class Movie {
    private String id;
    private String title;
    private int year;
    private String director;
    private float rating;
    private Map<String,String> starIDMap;
    private List<String> Genres;

    public Map<String, String> getStarIDMap() {
        return starIDMap;
    }

    public void setStarIDMap(Map<String, String> starIDMap) {
        this.starIDMap = starIDMap;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    public List<String> getGenres() {
        return Genres;
    }

    public void setGenres(List<String> genres) {
        Genres = genres;
    }


}
