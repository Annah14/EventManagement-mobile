package com.example.annaheventsls.models;

public class Booking {
    private int id;
    private int user_id;
    private String event_date;
    private String package_type;
    private String event_type;
    private String venue;
    private int guests;
    private String message;
    private String payment_method;
    private String payment_pin;
    private String status;
    private String created_at;
    private String fullname; // Joined from users table
    private String email;    // Joined from users table

    public int getId() { return id; }
    public int getUserId() { return user_id; }
    public String getEventDate() { return event_date; }
    public String getPackageType() { return package_type; }
    public String getEventType() { return event_type; }
    public String getVenue() { return venue; }
    public int getGuests() { return guests; }
    public String getMessage() { return message; }
    public String getPaymentMethod() { return payment_method; }
    public String getPaymentPin() { return payment_pin; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return created_at; }
    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
}
