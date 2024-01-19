package com.example.circuit.Models;

public class Item {
    String Name, Description, ImageLink, Price;

    public Item() {
    }

    public Item(String name, String description, String imageLink, String price) {
        Name = name;
        Description = description;
        ImageLink = imageLink;
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
