package com.example.hotelreservationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class PersonalInfoActivity extends AppCompatActivity {

    EditText fullNameEditText, emailEditText, phoneEditText, addressEditText, idEditText, birthDateEditText;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Initialize EditText fields
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        idEditText = findViewById(R.id.idEditText);
        birthDateEditText = findViewById(R.id.birthDateEditText);

        // Initialize the "Save and Continue" button
        saveButton = findViewById(R.id.saveButton);

        // Add TextWatcher to birthDateEditText for formatting the date
        birthDateEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }

                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int month = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (month > 12) month = 12;
                        cal.set(Calendar.MONTH, month - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, month, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    birthDateEditText.setText(current);
                    birthDateEditText.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the entered personal information
                String fullName = fullNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String id = idEditText.getText().toString().trim();
                String birthDate = birthDateEditText.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)
                        || TextUtils.isEmpty(address) || TextUtils.isEmpty(id) || TextUtils.isEmpty(birthDate)) {
                    Toast.makeText(PersonalInfoActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate email format
                if (!isValidEmail(email)) {
                    emailEditText.setError("Invalid email format");
                    return;
                }

                // Proceed to the next step, such as confirming the booking or selecting dates
                Intent intent = new Intent(PersonalInfoActivity.this, RoomInfoActivity.class);
                intent.putExtra("fullName", fullName);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                intent.putExtra("address", address);
                intent.putExtra("id", id);
                intent.putExtra("birthDate", birthDate);
                startActivity(intent);
            }
        });
    }

    // Email validation method
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
