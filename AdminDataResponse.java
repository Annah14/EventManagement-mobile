package com.example.annaheventsls.models;

import java.util.List;
import java.util.Map;

public class AdminDataResponse {
    private List<Booking> bookings;
    private List<User> users;
    private List<Inquiry> inquiries;
    private List<Map<String, Object>> services;
    private List<Map<String, Object>> packages;

    public List<Booking> getBookings() { return bookings; }
    public List<User> getUsers() { return users; }
    public List<Inquiry> getInquiries() { return inquiries; }
    public List<Map<String, Object>> getServices() { return services; }
    public List<Map<String, Object>> getPackages() { return packages; }
}
