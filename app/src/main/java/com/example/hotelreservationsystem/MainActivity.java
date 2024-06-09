package com.example.hotelreservationsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button btnBookNowSingleBed;
    Button btnBookNowDoubleBed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnBookNowSingleBed = findViewById(R.id.btnBookNowSingleBed);
        btnBookNowSingleBed.setOnClickListener(v -> {
            // Clear user data from shared preferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Launch the LoginActivity when the Book Now for Single Bed button is clicked
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        });

        btnBookNowDoubleBed = findViewById(R.id.btnBookNowDoubleBed);
        btnBookNowDoubleBed.setOnClickListener(v -> {
            // Clear user data from shared preferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Launch the LoginActivity when the Book Now for Double Bed button is clicked
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
