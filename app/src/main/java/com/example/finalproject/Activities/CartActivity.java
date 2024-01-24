package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.finalproject.Adapters.CartAdapter;
import com.example.finalproject.Adapters.OnChangeQualityListener;
import com.example.finalproject.Database.CartDAO;
import com.example.finalproject.Firebase.FirebaseManager;
import com.example.finalproject.Models.Cart;
import com.example.finalproject.Models.OnGetDataByIdListener;
import com.example.finalproject.Models.Product;
import com.example.finalproject.R;
import com.example.finalproject.Utils.CommonActivity;
import com.example.finalproject.Utils.DialogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CartActivity extends AppCompatActivity {
    private Context mContext;

    private CartDAO cartDAO;
    private CartAdapter adapter;
    private List<Cart> list;
    private String ID_USER;

    private Toolbar toolBar;
    private RecyclerView rcyCart;
    private TextView tvTotal;
    private TextView tvQuality;
    private AppCompatButton btnCheckOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initData();
    }

    private void initData() {
        FirebaseManager.init();
        mContext = CartActivity.this;
        cartDAO = new CartDAO(mContext);

        toolBar = findViewById(R.id.tool_bar);
        rcyCart = findViewById(R.id.rcy_cart);
        tvTotal = findViewById(R.id.tv_total);
        tvQuality = findViewById(R.id.tv_quality);
        btnCheckOut = findViewById(R.id.btn_checkout);

        setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(v -> finish());
        rcyCart.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        rcyCart.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        getData();
        btnCheckOut.setOnClickListener(v -> checkout());
    }

    private void getData() {
        ID_USER = getIntent().getStringExtra("ID_USER");
        list = cartDAO.getAllData();
        if (!list.isEmpty()) {
            List<String> idProducts = list.stream().map(Cart::getIdProduct).collect(Collectors.toList());
            FirebaseManager.getProductsByIds(idProducts, new OnGetDataByIdListener() {
                @Override
                public void onDataListLoaded(List<Product> dataList) {
                    List<Integer> positionsToRemove = new ArrayList<>();
                    int u = 0;
                    int r = 0;

                    for (int i = 0; i < dataList.size(); i++) {
                        Product p = dataList.get(i);

                        if (p.getInventory() == 0) {
                            positionsToRemove.add(i);
                            r += cartDAO.delete(idProducts.get(i));
                        }
                    }

                    Collections.reverse(positionsToRemove);
                    for (int position : positionsToRemove) {
                        list.remove(position);
                        dataList.remove(position);
                        idProducts.remove(position);
                    }

                    for (int i = 0; i < dataList.size(); i++) {
                        Product p = dataList.get(i);
                        Cart c = list.get(i);

                        if (p.getInventory() < c.getQuality()) {
                            u = cartDAO.update(c.getIdProduct(), c.getQuality() - (c.getQuality() - p.getInventory()));
                        }
                    }

                    if (u > 0) {
                        list.clear();
                        list.addAll(cartDAO.getAllData());
                        if (r == 0) {
                            DialogUtils.showDialog(mContext, "We have just updated the " +
                                    "quantity in your cart as the remaining quantity of the product has changed");
                        }
                    }
                    else {
                        if (r > 0) {
                            DialogUtils.showDialog(mContext, "We have just updated the " +
                                    "quantity in your cart as the remaining quantity of the product has changed");
                        }
                    }

                    if (!list.isEmpty()) {
                        adapter = new CartAdapter(mContext, list, dataList, (carts, products)
                                -> calculator(carts, products));
                        rcyCart.setAdapter(adapter);
                        calculator(list, dataList);
                    }
                }

                @Override
                public void onDataListError(String errorMessage) {
                    DialogUtils.showDialog(mContext, "Get data error. " + errorMessage);
                }
            });
        }
    }

    private void checkout() {
        if (list.isEmpty()) {
            DialogUtils.showDialog(mContext, "Your cart is nothing! Please add product to your cart.");
        }
        else {
            Intent intent = new Intent(mContext, CheckoutActivity.class);
            intent.putExtra("ID_USER", ID_USER);
            startActivity(intent);
            finish();
        }
    }

    private void calculator(List<Cart> carts, List<Product> products) {
        long quality = 0;
        long currency = 0;
        if (!carts.isEmpty() || !products.isEmpty()) {
            for (int i = 0; i < carts.size(); i++) {
                quality += list.get(i).getQuality();
                currency += list.get(i).getQuality() * products.get(i).getPrice();
            }
        }
        tvQuality.setText("( " + quality + " product )");
        tvTotal.setText(CommonActivity.currencyFormat(currency) + "đ");
    }
}