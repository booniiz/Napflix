package edu.uci.ics.fabflixmobile;
/**
 * This project is extended and modify from the android exmaple provided by the instruction team.
 * Permission acquire in 1160 on piazza.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SingleMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single);
        Intent intent = getIntent();
        TextView titleValue = findViewById(R.id.titleValue);
        TextView yearValue = findViewById(R.id.yearValue);
        TextView directorValue = findViewById(R.id.directorValue);
        TextView starsValue = findViewById(R.id.starsValue);
        TextView genresValue = findViewById(R.id.genresValue);
        titleValue.setText(intent.getStringExtra("title"));
        yearValue.setText(intent.getStringExtra("year"));
        directorValue.setText(intent.getStringExtra("director"));
        starsValue.setText(intent.getStringExtra("stars"));
        genresValue.setText(intent.getStringExtra("genre"));
    }
}
