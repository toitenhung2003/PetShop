package com.example.finalproject.Adapters;

import com.example.finalproject.Models.Cart;
import com.example.finalproject.Models.Product;

import java.util.List;

public interface OnChangeQualityListener {
    void onChangeQuality(List<Cart> carts, List<Product> products);
}
