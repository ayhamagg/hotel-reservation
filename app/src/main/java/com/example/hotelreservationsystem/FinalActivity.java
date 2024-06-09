package com.example.hotelreservationsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FinalActivity extends AppCompatActivity {

    TextView bookingInfoTextView, customerServiceTextView;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        bookingInfoTextView = findViewById(R.id.bookingInfoTextView);
        customerServiceTextView = findViewById(R.id.customerServiceTextView);
        backButton = findViewById(R.id.backButton);

        // Get data from intent
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("fullName");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String selectedRoomType = intent.getStringExtra("selectedRoomType");
        int roomPrice = intent.getIntExtra("roomPrice", 0);
        String checkInDate = intent.getStringExtra("checkInDate");
        String checkOutDate = intent.getStringExtra("checkOutDate");
        String numGuests = intent.getStringExtra("numGuests");
        String numRooms = intent.getStringExtra("numRooms");
        String totalPrice = intent.getStringExtra("totalPrice");
        boolean fromLogin = intent.getBooleanExtra("fromLogin", false);

        // Display booking information
        String bookingInfo = String.format(
                "\nHi %s,\n\nHere is your booking information:\n\n" +
                        "Room Type: %s\n" +
                        "Room Price: $%d\n" +
                        "Check-in Date: %s\n" +
                        "Check-out Date: %s\n" +
                        "Number of Guests: %s\n" +
                        "Number of Rooms: %s\n" +
                        "Total Price: %s\n\n" +
                        "Email: %s\n" +
                        "Phone: %s\n\n" +
                        "We hope you have a great stay at our hotel!",
                fullName, selectedRoomType, roomPrice, checkInDate, checkOutDate, numGuests, numRooms, totalPrice, email, phone);

        bookingInfoTextView.setText(bookingInfo);

        // Add customer service note
        String customerServiceNote = "For any assistance, please contact our customer service at +905343679975.";
        customerServiceTextView.setText(customerServiceNote);

        // Set up back button to navigate to HomeActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save username in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", fullName);
                editor.apply();

                // Navigate to HomeActivity
                Intent homeIntent = new Intent(FinalActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish(); // Finish FinalActivity to prevent returning to it when pressing back from HomeActivity
            }
        });

        // HTTP request to upload booking info to the database
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.148.62/insert_reservation.php"); // use 10.0.2.2 for localhost in Android emulator
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    String postData = "fullName=" + fullName + "&email=" + email + "&phone=" + phone +
                            "&roomType=" + selectedRoomType + "&roomPrice=" + roomPrice + "&checkInDate=" + checkInDate +
                            "&checkOutDate=" + checkOutDate + "&numGuests=" + numGuests + "&numRooms=" + numRooms +
                            "&totalPrice=" + totalPrice;

                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Handle response here if needed
                    } else {
                        // Handle error response here
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
