package com.example.finalproject.Models;

public class Cart {
    private int id;
    private String idProduct;
    private long quality;

    public static final String TB_NAME = "Carts";
    public static final String CL_ID = "ID";
    public static final String CL_ID_PRODUCT = "ID_Product";
    public static final String CL_QUALITY = "Quality";

    public Cart() {
    }

    public Cart(String idProduct, long quality) {
        this.idProduct = idProduct;
        this.quality = quality;
    }

    public Cart(int id, String idProduct, long quality) {
        this.id = id;
        this.idProduct = idProduct;
        this.quality = quality;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public long getQuality() {
        return quality;
    }

    public void setQuality(long quality) {
        this.quality = quality;
    }
}
