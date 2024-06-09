package com.example.hotelreservationsystem.model;

public class UserData {
    private String fullName;
    private String email;
    private String phone;
    private String roomType;
    private int roomPrice;
    private String checkInDate;
    private String checkOutDate;

    public UserData(String fullName, String email, String phone, String roomType, int roomPrice, String checkInDate, String checkOutDate) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    // Getters and setters (generated or manually added)
}
