package com.example.annaheventsls.models;

public class BookingRequest {
    private String event_date;
    private String event_type;
    private String venue;
    private int guests;
    private String message;
    private String package_type;
    private String payment_method;
    private String payment_pin;

    public BookingRequest(String event_date, String event_type, String venue, int guests, String message, String package_type, String payment_method, String payment_pin) {
        this.event_date = event_date;
        this.event_type = event_type;
        this.venue = venue;
        this.guests = guests;
        this.message = message;
        this.package_type = package_type;
        this.payment_method = payment_method;
        this.payment_pin = payment_pin;
    }
}
