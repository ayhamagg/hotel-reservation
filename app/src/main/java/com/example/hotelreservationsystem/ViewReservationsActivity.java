package com.example.hotelreservationsystem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ViewReservationsActivity extends AppCompatActivity {

    ListView reservationsListView;
    ArrayAdapter<String> adapter;
    ArrayList<String> reservationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewreservationsactivity);

        reservationsListView = findViewById(R.id.reservationsListView);
        reservationsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reservationsList);
        reservationsListView.setAdapter(adapter);

        // Retrieve data from the database
        new RetrieveReservationsTask().execute();
    }

    private class RetrieveReservationsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL("http://10.0.2.2/retrieve_reservations.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    inputStream.close();
                    reader.close();
                } else {
                    response.append("Error: ").append(responseCode);
                }

                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                response.append("Error: ").append(e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parseJSON(result);
        }
    }

    private void parseJSON(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            reservationsList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject reservation = jsonArray.getJSONObject(i);
                String reservationInfo = "Name: " + reservation.getString("fullName") + "\n" +
                        "Email: " + reservation.getString("email") + "\n" +
                        "Phone: " + reservation.getString("phone") + "\n" +
                        "Room Type: " + reservation.getString("roomType") + "\n" +
                        "Room Price: $" + reservation.getInt("roomPrice") + "\n" +
                        "Check-in Date: " + reservation.getString("checkInDate") + "\n" +
                        "Check-out Date: " + reservation.getString("checkOutDate") + "\n" +
                        "Number of Guests: " + reservation.getString("numGuests") + "\n" +
                        "Number of Rooms: " + reservation.getString("numRooms") + "\n" +
                        "Total Price: " + reservation.getString("totalPrice");
                reservationsList.add(reservationInfo);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
