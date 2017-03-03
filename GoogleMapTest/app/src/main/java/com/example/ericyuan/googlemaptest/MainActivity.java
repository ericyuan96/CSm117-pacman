package com.example.ericyuan.googlemaptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //called after clicking the "show map" button, starts a new Google Maps intent
    public void displayMap(View view) {

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
