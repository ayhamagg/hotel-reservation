package com.example.hotelreservationsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText editTextFullName, editTextEmail, editTextPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered user information
                String fullName = editTextFullName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validate the inputs
                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    // Show a toast message if any field is empty
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {
                    // Show an error message if the email is invalid
                    editTextEmail.setError("Invalid email format");
                } else {
                    // Save user data to shared preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("fullName", fullName);
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();

                    // Start HomeActivity and pass the user information
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish(); // Finish LoginActivity to prevent returning to it when pressing back
                }
            }
        });

        // Load saved user data if exists
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String savedFullName = sharedPreferences.getString("fullName", "");
        String savedEmail = sharedPreferences.getString("email", "");

        if (!savedFullName.isEmpty() && !savedEmail.isEmpty()) {
            editTextFullName.setText(savedFullName);
            editTextEmail.setText(savedEmail);
        }
    }

    // Email validation method
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
