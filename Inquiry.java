package com.example.annaheventsls.models;

public class Inquiry {
    private int id;
    private String fullname;
    private String email;
    private String subject;
    private String message;
    private String admin_reply;
    private String created_at;

    public int getId() { return id; }
    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public String getAdminReply() { return admin_reply; }
    public String getCreatedAt() { return created_at; }
}
