package com.example.ericyuan.googlemaptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        Intent in2 = getIntent();
        int score = in2.getIntExtra("map.score",0);
        int distance = in2.getIntExtra("map.distance",0);
        TextView finalscore = (TextView) findViewById(R.id.textViewScore);
        TextView finaldistance = (TextView) findViewById(R.id.textViewDistance);
        finalscore.setText("Score: " + score);
        finaldistance.setText("Distance Traveled: " + distance+"m");
    }
    public void MainScreen(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
