package com.example.hotelreservationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private String fullName;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Restore user information if available
        if (savedInstanceState == null) {
            // Get user information from intent extras
            fullName = getIntent().getStringExtra("fullName");
            email = getIntent().getStringExtra("email");
        } else {
            // Restore user information from savedInstanceState
            fullName = savedInstanceState.getString("fullName");
            email = savedInstanceState.getString("email");
        }

        // Display user information in TextViews
        TextView fullNameTextView = findViewById(R.id.fullNameTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        fullNameTextView.setText("User: " + fullName);
        emailTextView.setText("Email: " + email);

        // Get reference to the "Continue to Booking" button
        Button continueButton = findViewById(R.id.continueButton);

        // Set OnClickListener for the "Continue to Booking" button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to PersonalInfoActivity
                Intent intent = new Intent(HomeActivity.this, PersonalInfoActivity.class);
                intent.putExtra("fullName", fullName);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save user information to restore later if needed
        outState.putString("fullName", fullName);
        outState.putString("email", email);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore user information from savedInstanceState
        fullName = savedInstanceState.getString("fullName");
        email = savedInstanceState.getString("email");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user information is still valid or needs to be refreshed
        if (fullName == null || email == null) {
            // Optionally handle the case where user information is missing
            Toast.makeText(this, "User information missing", Toast.LENGTH_SHORT).show();
            // Example: Redirect to login screen or perform necessary actions
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close current activity to prevent returning here when pressing back
        }
    }
}
