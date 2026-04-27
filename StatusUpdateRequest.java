package com.example.annaheventsls.models;

public class StatusUpdateRequest {
    private int booking_id;
    private String status;

    public StatusUpdateRequest(int booking_id, String status) {
        this.booking_id = booking_id;
        this.status = status;
    }
}
