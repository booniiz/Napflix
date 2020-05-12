
import jdk.nashorn.internal.ir.WhileNode;
import org.w3c.dom.*;

import java.sql.*;
import java.util.*;

public class MovieParser {
    Set<Movie> movies = new HashSet<>();
    Set<String> genres = new HashSet<>();

    /**
     *
     * @param dom
     */
    public void parseDom(Document dom) {
        Element element = dom.getDocumentElement();
        NodeList nodeList = element.getElementsByTagName("film");
        processFilms(nodeList);
        for (Movie m: this.movies){
            genres.addAll(m.getGenres());
        }
        insertToDatabase();
    }

//    public String getDirectorName(NodeList directorfilmsChildNodeList){
//        for (int j = i
//    }

    public void processFilms(NodeList filmsNodes){
        for(int i = 0; i < filmsNodes.getLength(); i++){
            processFilm(filmsNodes.item(i));
        }
    }

    public void processFilm(Node film){
        Element elem = (Element) film;
        String title = null;
        if (elem.getElementsByTagName("t").getLength() > 0){
            title = elem.getElementsByTagName("t").item(0).getTextContent();
        }else{
            //System.out.println("A movie didn't have a title");
        }
        String id = null;
        if (elem.getElementsByTagName("fid").getLength() > 0){
            id = elem.getElementsByTagName("fid").item(0).getTextContent();
        }else{
            //System.out.printf("\"%s\" didn't have an id \n", title);
        }
        int year = 0;
        if (elem.getElementsByTagName("year").getLength() > 0){
            try{
                year = Integer.parseInt(elem.getElementsByTagName("year").item(0).getTextContent());
            }catch (NumberFormatException e){
                //System.out.printf("\"%s\" didn't have an parseable year(NumberFormatException because of %s) \n", title, (elem.getElementsByTagName("year").item(0).getTextContent()));
            }
        }else{
            //System.out.printf("\"%s\" didn't have an year \n", title);
        }
        String director = null;
        if (elem.getElementsByTagName("dirn").getLength() > 0){
            director = elem.getElementsByTagName("dirn").item(0).getTextContent();
        }else{
            System.out.printf("%s didn't have a director \n", title);
        }
        List<String> genres = getGenres(elem.getElementsByTagName("cats"));

        this.movies.add(new Movie(id, title, year, director, genres));
    }

    /**
     *
     * @param catNodeList
     * @return
     */
    public List<String> getGenres(NodeList catNodeList){
        HashMap<String, String> genresCode = new HashMap<>();
        genresCode.putIfAbsent("Ctxx".toLowerCase(),"Uncategorized");
        genresCode.putIfAbsent("Actn".toLowerCase(),"Violence");
        genresCode.putIfAbsent("Camp".toLowerCase(),"Now - camp");
        genresCode.putIfAbsent("Comd".toLowerCase(),"Comedy");
        genresCode.putIfAbsent("Epic".toLowerCase(),"Epic");
        genresCode.putIfAbsent("Horr".toLowerCase(),"Horror");
        genresCode.putIfAbsent("Noir".toLowerCase(), "Black");
        genresCode.putIfAbsent("ScFi".toLowerCase(), "Sci-Fi");   //Merging Sci-Fi with science fiction
        genresCode.putIfAbsent("West".toLowerCase(), "Western");
        genresCode.putIfAbsent("Advt".toLowerCase(), "Adventure");
        genresCode.putIfAbsent("Cart".toLowerCase(), "Cartoon");
        genresCode.putIfAbsent("Docu".toLowerCase(), "Documentary");
        genresCode.putIfAbsent("Faml".toLowerCase(),"Family");
        genresCode.putIfAbsent("Musc".toLowerCase(),"Musical");
        genresCode.putIfAbsent("Porn".toLowerCase(), "Pornography");
        genresCode.putIfAbsent("Surl".toLowerCase(), "Sureal");
        genresCode.putIfAbsent("AvGa".toLowerCase(), "Avant Garde");
        genresCode.putIfAbsent("CnR".toLowerCase(), "Cops and Robbers");
        genresCode.putIfAbsent("Dram".toLowerCase(), "Drama");
        genresCode.putIfAbsent("Hist".toLowerCase(), "History");
        genresCode.putIfAbsent("Myst".toLowerCase(), "Mystery");
        genresCode.putIfAbsent("Romt".toLowerCase(), "Romantic");
        genresCode.putIfAbsent("Susp".toLowerCase(), "Thriller");
        genresCode.putIfAbsent("BioP".toLowerCase(), "Biographical Picture");
        genresCode.putIfAbsent("TV".toLowerCase(), "TV Show");
        genresCode.putIfAbsent("TVs".toLowerCase(), "TV Series");
        genresCode.putIfAbsent("TVm".toLowerCase(), "TV Miniseries");

        Set<String> genres = new HashSet<>();
        if (catNodeList == null){
            return (List<String>) genres;
        }
        for(int i = 0; i < catNodeList.getLength(); i++) {
            String temp = catNodeList.item(i).getTextContent().toLowerCase().replaceAll("\\s","");
            if(temp.length() == 14){
                genres.add(genresCode.getOrDefault(temp.substring(0,4), temp.substring(0,4)));
                genres.add(genresCode.getOrDefault(temp.substring(5,8), temp.substring(5,8)));
                genres.add(genresCode.getOrDefault(temp.substring(9,14), temp.substring(9,14)));
            }else if(temp.length() == 12){
                genres.add(genresCode.getOrDefault(temp.substring(0,4), temp.substring(0,4)));
                genres.add(genresCode.getOrDefault(temp.substring(4,8), temp.substring(4,8)));
                genres.add(genresCode.getOrDefault(temp.substring(4,8), temp.substring(8,12)));
            }else if (temp.length() == 8){
                genres.add(genresCode.getOrDefault(temp.substring(0,4), temp.substring(0,4)));
                genres.add(genresCode.getOrDefault(temp.substring(4,8), temp.substring(4,8)));
            }else if(temp.length() == 9) {
                genres.add(genresCode.getOrDefault(temp.substring(0, 4), temp.substring(0, 4)));
                genres.add(genresCode.getOrDefault(temp.substring(5, 9), temp.substring(5, 9)));
            }else if(genresCode.containsKey(temp.replaceAll("\\s",""))){

            }else{
                if (temp != ""){
                    genres.add(genresCode.getOrDefault(temp,temp));
                }
            }
        }
        return new ArrayList<>(genres);
    }

    public void insertToDatabase(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            databaseAuthentication da = new databaseAuthentication();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            // User can't not access this.
            for (String s: new ArrayList<>(this.genres)){
                String sqlStatement = "INSERT INTO genres(name) SELECT \"" + s
                        + "\" WHERE \"" + s + "\" NOT IN (SELECT name FROM genres)";
                Statement statement = conn.createStatement();
                statement.executeUpdate(sqlStatement);
                statement.close();
            }
            for (Movie m: new ArrayList<>(this.movies)){
                if (m.getId() == null){
                    System.out.println(m.getTitle() + "'s ID is null");
                }else if (m.getTitle() == null){
                    System.out.println(m.getId() + "'s title is null");
                }else if (m.getYear() == -1){
                    System.out.println(m.getTitle() + "'s year is -1(invalid)");
                }else if (m.getDirector() == null){
                    System.out.println(m.getTitle() + "'s director is null");
                }else{
                    String temp ="INSERT INTO movies SELECT \""+ m.getId() +"\",\""+ m.getTitle() +"\",\""+ m.getYear() +"\",\""+ m.getDirector() +"\" WHERE \"" + m.getId() + "\" NOT IN (SELECT ID FROM movies)";
                    Statement statement = conn.createStatement();
                    statement.executeUpdate(temp);
                    statement.close();
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String,Integer> genreIDMap(){
        HashMap<String,Integer> out = new HashMap<>();
        String sqlStatement = "SELECT *\n" +
                "FROM genres";
        try{
            databaseAuthentication da = new databaseAuthentication();
            Connection conn = DriverManager.getConnection(da.getAddress(),da.getUsername(), da.getPassowrd());
            Statement s = conn.createStatement();
            ResultSet resultSet = s.executeQuery(sqlStatement);
            while(resultSet.next()){
                out.put(resultSet.getString(2),resultSet.getInt(1));
            }
            resultSet.close();
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;

    };
}
