package com.example.hotelreservationsystem.network;

import com.example.hotelreservationsystem.model.ResponseModel;
import com.example.hotelreservationsystem.model.UserData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("insert_reservation.php") // Adjust endpoint as per your PHP script
    Call<ResponseModel> sendUserData(@Body UserData userData);
}
