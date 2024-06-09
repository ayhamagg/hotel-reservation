package com.example.hotelreservationsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotelreservationsystem.model.UserData;
import com.example.hotelreservationsystem.network.ApiService;
import com.example.hotelreservationsystem.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomInfoActivity extends AppCompatActivity {

    Spinner roomTypeSpinner, numGuestsSpinner;
    EditText numRoomsEditText, checkInDateEditText, checkOutDateEditText;
    EditText cardNumberEditText, expirationDateEditText, cvvEditText; // Payment fields
    Button confirmButton, confirmContinueButton;
    TextView roomPriceTextView, roomAvailabilityTextView, totalPriceTextView;

    int[] roomPrices = {100, 150, 200, 250, 300}; // Example prices in dollars
    int feePerGuest = 50; // Additional fee per guest in dollars

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);

        // Initialize views
        roomTypeSpinner = findViewById(R.id.roomTypeSpinner);
        numGuestsSpinner = findViewById(R.id.numGuestsSpinner);
        numRoomsEditText = findViewById(R.id.numRoomsEditText);
        checkInDateEditText = findViewById(R.id.checkInDateEditText);
        checkOutDateEditText = findViewById(R.id.checkOutDateEditText);
        cardNumberEditText = findViewById(R.id.cardNumberEditText); // Payment fields
        expirationDateEditText = findViewById(R.id.expirationDateEditText); // Payment fields
        cvvEditText = findViewById(R.id.cvvEditText); // Payment fields
        confirmButton = findViewById(R.id.checkButton);
        confirmContinueButton = findViewById(R.id.confirmContinueButton);
        roomPriceTextView = findViewById(R.id.roomPriceTextView);
        roomAvailabilityTextView = findViewById(R.id.roomAvailabilityTextView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        // Setup room type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.room_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomTypeSpinner.setAdapter(adapter);

        // Set OnClickListener for confirm button
        confirmButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String selectedRoomType = roomTypeSpinner.getSelectedItem().toString();
                int roomPrice = getRoomPrice(selectedRoomType);
                roomPriceTextView.setText(getString(R.string.room_price_display, roomPrice));
                roomAvailabilityTextView.setText("Room is available");
                roomAvailabilityTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                calculateTotalPrice(roomPrice);
                Toast.makeText(RoomInfoActivity.this, "Checking...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RoomInfoActivity.this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for confirm and continue button
        confirmContinueButton.setOnClickListener(v -> {
            if (validateInputs() && validatePaymentDetails()) {
                String selectedRoomType = roomTypeSpinner.getSelectedItem().toString();
                int roomPrice = getRoomPrice(selectedRoomType);
                String checkInDate = checkInDateEditText.getText().toString();
                String checkOutDate = checkOutDateEditText.getText().toString();
                String numGuests = numGuestsSpinner.getSelectedItem().toString();
                String numRooms = numRoomsEditText.getText().toString();
                calculateTotalPrice(roomPrice);
                Toast.makeText(RoomInfoActivity.this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RoomInfoActivity.this, FinalActivity.class);
                intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("phone", getIntent().getStringExtra("phone"));
                intent.putExtra("selectedRoomType", selectedRoomType);
                intent.putExtra("roomPrice", roomPrice);
                intent.putExtra("checkInDate", checkInDate);
                intent.putExtra("checkOutDate", checkOutDate);
                intent.putExtra("numGuests", numGuests);
                intent.putExtra("numRooms", numRooms);
                intent.putExtra("totalPrice", totalPriceTextView.getText().toString());
                intent.putExtra("cardNumber", cardNumberEditText.getText().toString());
                intent.putExtra("expirationDate", expirationDateEditText.getText().toString());
                intent.putExtra("cvv", cvvEditText.getText().toString());
                startActivity(intent);

                // Send reservation data to server
                sendReservationDataToServer();
            } else {
                Toast.makeText(RoomInfoActivity.this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show();
            }
        });

        // TextWatcher for card number to limit to 16 digits
        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 16) {
                    cardNumberEditText.setText(s.subSequence(0, 16));
                    cardNumberEditText.setSelection(16);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // TextWatcher for expiration date to add '/' after two characters
        expirationDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2 && before == 0) {
                    expirationDateEditText.setText(s + "/");
                    expirationDateEditText.setSelection(s.length() + 1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // TextWatcher for CVV to limit to 3 or 4 digits
        cvvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4) {
                    cvvEditText.setText(s.subSequence(0, 4));
                    cvvEditText.setSelection(4);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Set OnClickListener for check-in date
        checkInDateEditText.setOnClickListener(v -> showDatePickerDialog(checkInDateEditText));

        // Set OnClickListener for check-out date
        checkOutDateEditText.setOnClickListener(v -> showDatePickerDialog(checkOutDateEditText));
    }

    private void showDatePickerDialog(EditText dateEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(RoomInfoActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", month1 + 1, dayOfMonth, year1);
                    dateEditText.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private boolean validateInputs() {
        return !numRoomsEditText.getText().toString().isEmpty()
                && !checkInDateEditText.getText().toString().isEmpty()
                && !checkOutDateEditText.getText().toString().isEmpty();
    }

    private boolean validatePaymentDetails() {
        String cardNumber = cardNumberEditText.getText().toString().trim();
        String expirationDate = expirationDateEditText.getText().toString().trim();
        String cvv = cvvEditText.getText().toString().trim();

        if (cardNumber.length() != 16) {
            cardNumberEditText.setError("Card number must be 16 digits");
            return false;
        }

        if (!expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            expirationDateEditText.setError("Expiration date must be MM/YY");
            return false;
        }

        if (cvv.length() != 3 && cvv.length() != 4) {
            cvvEditText.setError("CVV must be 3 or 4 digits");
            return false;
        }

        return true;
    }

    private int getRoomPrice(String roomType) {
        switch (roomType) {
            case "Single Room":
                return roomPrices[0];
            case "Double Room":
                return roomPrices[1];
            case "Double Room Deluxe":
                return roomPrices[2];
            case "Suite":
                return roomPrices[3];
            case "Presidential Suite":
                return roomPrices[4];
            default:
                return 0;
        }
    }

    private void calculateTotalPrice(int roomPrice) {
        int numRooms = Integer.parseInt(numRoomsEditText.getText().toString());
        int numGuests = Integer.parseInt(numGuestsSpinner.getSelectedItem().toString());

        String checkInDate = checkInDateEditText.getText().toString();
        String checkOutDate = checkOutDateEditText.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);

            if (checkIn != null && checkOut != null && !checkOut.before(checkIn)) {
                long diffInMillis = checkOut.getTime() - checkIn.getTime();
                long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

                if (diffInDays == 0) {
                    diffInDays = 1;
                }

                int totalPrice = (int) ((numRooms * roomPrice * diffInDays) + (numGuests * feePerGuest * diffInDays));
                totalPriceTextView.setText(getString(R.string.total_price_display, totalPrice));
            } else {
                totalPriceTextView.setText("");
                Toast.makeText(this, "Invalid check-in or check-out date", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void sendReservationDataToServer() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "http://192.168.148.62/hotelreservation/insert.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle the response from the server
                    Toast.makeText(RoomInfoActivity.this, "Data sent to server", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle any errors
                    Toast.makeText(RoomInfoActivity.this, "Error sending data to server", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("fullName", getIntent().getStringExtra("fullName"));
                params.put("email", getIntent().getStringExtra("email"));
                params.put("phone", getIntent().getStringExtra("phone"));
                params.put("selectedRoomType", roomTypeSpinner.getSelectedItem().toString());
                params.put("roomPrice", String.valueOf(getRoomPrice(roomTypeSpinner.getSelectedItem().toString())));
                params.put("checkInDate", checkInDateEditText.getText().toString());
                params.put("checkOutDate", checkOutDateEditText.getText().toString());
                params.put("numGuests", numGuestsSpinner.getSelectedItem().toString());
                params.put("numRooms", numRoomsEditText.getText().toString());
                params.put("totalPrice", totalPriceTextView.getText().toString().replace("$", ""));
                params.put("cardNumber", cardNumberEditText.getText().toString());
                params.put("expirationDate", expirationDateEditText.getText().toString());
                params.put("cvv", cvvEditText.getText().toString());

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
