package com.example.finalproject.Models;

public class Product {
    private String id;
    private String name;
    private long price;
    private String desc;
    private String image;
    private long inventory;

    public Product() {
    }

    public Product(String id, String name, long price, String desc, String image, long inventory) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.image = image;
        this.inventory = inventory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getInventory() {
        return inventory;
    }

    public void setInventory(long inventory) {
        this.inventory = inventory;
    }
}
