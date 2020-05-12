import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Movie {
    private String id;
    private String title;
    private int year;
    private String director;
    private float rating;
    private Map<String,String> starIDMap;
    private List<String> Genres;

    public Movie() {
    }

    public Movie(String id, String title, int year, String director, float rating, Map<String, String> starIDMap, List<String> genres) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.starIDMap = starIDMap;
        Genres = genres;
    }

    public Movie(String id, String title, int year, String director, List<String> genres){
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.Genres = genres;
    }

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

    //Sources: autocomplete/implemen by intellJ
    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", director='" + director + '\'' +
                ", rating=" + rating +
                ", starIDMap=" + starIDMap +
                ", Genres=" + Genres +
                '}';
    }

    // Sources: autocomplete/implemen by intellJ
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return year == movie.year &&
                Float.compare(movie.rating, rating) == 0 &&
                Objects.equals(id, movie.id) &&
                Objects.equals(title, movie.title) &&
                Objects.equals(director, movie.director) &&
                Objects.equals(starIDMap, movie.starIDMap) &&
                Objects.equals(Genres, movie.Genres);
    }

    // Sources: autocomplete/implement by intellJ
    @Override
    public int hashCode() {
        return Objects.hash(id, title, year, director, rating, starIDMap, Genres);
    }
}
