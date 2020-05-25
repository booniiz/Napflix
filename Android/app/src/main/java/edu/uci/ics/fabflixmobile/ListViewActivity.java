package edu.uci.ics.fabflixmobile;
/**
 * This project is extended and modify from the android exmaple provided by the instruction team.
 * Permission acquire in 1160 on piazza.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends Activity {
    private EditText searchBox;
    private Button searchButton;
    private Button prevButton;
    private Button nextButton;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        searchBox = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.Search);
        prevButton = findViewById(R.id.prev25);
        nextButton = findViewById(R.id.next25);
        prevButton.setEnabled(false);

        ArrayList<Movie> movies = new ArrayList<>();
        //Search
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                page = 1;
                requestMovies(searchBox.getText().toString(),ListViewActivity.this.page);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListViewActivity.this.page += 1;
                requestMovies(searchBox.getText().toString(),ListViewActivity.this.page);
                if (ListViewActivity.this.page >= 2){
                    prevButton.setEnabled(true);
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListViewActivity.this.page -= 1;
                requestMovies(searchBox.getText().toString(),ListViewActivity.this.page);
                if (ListViewActivity.this.page <= 1){
                    prevButton.setEnabled(false);
                }
            }
        });

    }
    public void setListView(ArrayList<Movie> in){
        MovieListViewAdapter adapter = new MovieListViewAdapter(in, ListViewActivity.this);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = in.get(position);
                Intent intent = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                ArrayList<String> stars = new ArrayList<>(movie.getStarIDMap().values());
                intent.putExtra("title",movie.getTitle());
                intent.putExtra("year", String.valueOf(movie.getYear()));
                intent.putExtra("director", movie.getDirector());
                intent.putExtra("stars", String.join(",",stars));
                intent.putExtra("genre", String.join(",",movie.getGenres()));
                startActivity(intent);
            }
        });
    }
    public void requestMovies(String title, int page){
        final RequestQueue queue = NetworkManager.sharedManager(ListViewActivity.this).queue;
        final StringRequest movieListRequest = new StringRequest(Request.Method.GET, "http://ec2-18-234-101-122.compute-1.amazonaws.com:8443/Napflix/androidList?titleID="+ title +"&yearID&directorID&starID&sort=m.title%20ASC,r.rating%20DESC&page="+ String.valueOf(page) +"&limit=19", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Type movieArrayListType = new TypeToken<ArrayList<Movie>>() {
                }.getType();
                Gson gson = new Gson();
                setListView(gson.fromJson(s, movieArrayListType));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("HTTPError", volleyError.toString());
            }
        });
        queue.add(movieListRequest);

    }
}