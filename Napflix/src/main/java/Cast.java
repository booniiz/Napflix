import java.util.Objects;

public class Cast {
    private String id;
    private String name;
    private int birthYear;

    public Cast(String id, String name, int birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
    //Intellj auto implement
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cast cast = (Cast) o;
        return birthYear == cast.birthYear &&
                Objects.equals(id, cast.id) &&
                Objects.equals(name, cast.name);
    }
    //Intellj auto implement
    @Override
    public int hashCode() {
        return Objects.hash(id, name, birthYear);
    }
    //Intellj auto implement
    @Override
    public String toString() {
        return "Cast{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", birthYear=" + birthYear +
                '}';
    }
}
