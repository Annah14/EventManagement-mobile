package com.example.annaheventsls.models;

public class PackageRequest {
    private String name;
    private double price;
    private String short_desc;
    private String long_desc;
    private String icon_class;

    public PackageRequest(String name, double price, String short_desc, String long_desc, String icon_class) {
        this.name = name;
        this.price = price;
        this.short_desc = short_desc;
        this.long_desc = long_desc;
        this.icon_class = icon_class;
    }
}
