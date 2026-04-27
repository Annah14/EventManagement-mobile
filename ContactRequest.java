package com.example.annaheventsls.models;

public class ContactRequest {
    private String fullname;
    private String email;
    private String subject;
    private String message;

    public ContactRequest(String fullname, String email, String subject, String message) {
        this.fullname = fullname;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }
}
