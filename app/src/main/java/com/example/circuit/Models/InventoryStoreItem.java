package com.example.circuit.Models;


public class InventoryStoreItem {
    String name, imageLink, Description, price;

    public InventoryStoreItem() {
    }

    public InventoryStoreItem(String name, String imageLink, String description) {
        this.name = name;
        this.imageLink = imageLink;
        this.Description = description;
    }

    public InventoryStoreItem(String name, String imageLink, String description, String price) {
        this.name = name;
        this.imageLink = imageLink;
        Description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }



    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
