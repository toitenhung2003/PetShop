package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.Adapters.CartAdapter;
import com.example.finalproject.Adapters.ProductCheckoutAdapter;
import com.example.finalproject.Database.CartDAO;
import com.example.finalproject.Firebase.FirebaseManager;
import com.example.finalproject.Models.Cart;
import com.example.finalproject.Models.OnGetDataByIdListener;
import com.example.finalproject.Models.OnUpdateDataListener;
import com.example.finalproject.Models.OnWriteDataListener;
import com.example.finalproject.Models.Order;
import com.example.finalproject.Models.Product;
import com.example.finalproject.R;
import com.example.finalproject.Utils.CommonActivity;
import com.example.finalproject.Utils.DialogUtils;
import com.example.finalproject.Utils.ValidateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CheckoutActivity extends AppCompatActivity implements OnWriteDataListener, OnUpdateDataListener {
    private Context mContext;
    private CartDAO cartDAO;
    private ProductCheckoutAdapter adapter;
    private List<Cart> list;
    private List<Product> products;
    private boolean isOrder;
    private String ID_USER;
    private String ID_PRODUCT;
    private long QUALITY;
    private long total;
    private int resultUpdate;

    private Toolbar toolBar;
    private TextView tvInfo;
    private TextView tvNameUser;
    private TextView tvPhoneUser;
    private TextView tvAddressUser;
    private EditText edtNoteUser;
    private RecyclerView rcyProductUser;
    private TextView tvTotalItem;
    private TextView tvTotalPriceUser;
    private TextView tvFeeUser;
    private TextView tvTotalCheckOut;
    private TextView tvQualityProductCheckOut;
    private AppCompatButton btnOrder;

    private ConstraintLayout popUpConfirmInfo;
    private ImageView imgClosePopUp;
    private EditText edtPopUpNameUser;
    private EditText edtPopUpPhoneUser;
    private EditText edtPopUpAddressUser;
    private AppCompatButton btnConfirmInfoUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("PROVINCES", MODE_PRIVATE);
        String address = sharedPreferences.getString("DATA_PROVINCE", null);
        if (address != null) {
            edtPopUpAddressUser.setText(address);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("DATA_PROVINCE");
        editor.apply();
    }

    private void initData() {
        FirebaseManager.init();
        mContext = CheckoutActivity.this;
        cartDAO = new CartDAO(mContext);
        ID_USER = getIntent().getStringExtra("ID_USER");

        toolBar = (Toolbar) findViewById(R.id.tool_bar);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvNameUser = (TextView) findViewById(R.id.tv_name_user);
        tvPhoneUser = (TextView) findViewById(R.id.tv_phone_user);
        tvAddressUser = (TextView) findViewById(R.id.tv_address_user);
        edtNoteUser = (EditText) findViewById(R.id.edt_note_user);
        rcyProductUser = (RecyclerView) findViewById(R.id.rcy_product_user);
        tvTotalItem = (TextView) findViewById(R.id.tv_total_item);
        tvTotalPriceUser = (TextView) findViewById(R.id.tv_total_price_user);
        tvFeeUser = (TextView) findViewById(R.id.tv_fee_user);
        tvTotalCheckOut = (TextView) findViewById(R.id.tv_total_check_out);
        tvQualityProductCheckOut = (TextView) findViewById(R.id.tv_quality_product_check_out);
        btnOrder = (AppCompatButton) findViewById(R.id.btn_order);

        popUpConfirmInfo = (ConstraintLayout) findViewById(R.id.pop_up_confirm_info);
        imgClosePopUp = (ImageView) findViewById(R.id.img_close_pop_up);
        edtPopUpNameUser = (EditText) findViewById(R.id.edt_pop_up_name_user);
        edtPopUpPhoneUser = (EditText) findViewById(R.id.edt_pop_up_phone_user);
        edtPopUpAddressUser = (EditText) findViewById(R.id.edt_pop_up_address_user);
        btnConfirmInfoUser = (AppCompatButton) findViewById(R.id.btn_confirm_info_user);

        setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(v -> {
            if (isOrder) {
                Intent intent = new Intent(mContext, CartActivity.class);
                intent.putExtra("ID_USER", ID_USER);
                startActivity(intent);
            }
            finish();
        });
        rcyProductUser.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        rcyProductUser.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        getData();
        setPopUp();
        btnOrder.setOnClickListener(v -> {
            Order order = validate(1);
            if (order != null) {
                List<String> idProducts = list.stream().map(Cart::getIdProduct).collect(Collectors.toList());
                FirebaseManager.getProductsByIds(idProducts, new OnGetDataByIdListener() {
                    @Override
                    public void onDataListLoaded(List<Product> dataList) {
                        boolean check = true;
//                        for (int i = 0; i < dataList.size(); i++) {
//                            Product p = dataList.get(i);
//
//                            if (p.getInventory() == 0) {
//                                check = false;
//                                if (isOrder) {
//                                    DialogUtils.showDialog(mContext, "The quantity of some products " +
//                                                    "you purchased seems to have changed. Please go back to verify.",
//                                            (dialog, which) -> {
//                                                Intent intent = new Intent(mContext, CartActivity.class);
//                                                intent.putExtra("ID_USER", ID_USER);
//                                                startActivity(intent);
//                                                finish();
//                                            }, false);
//                                } else {
//                                    DialogUtils.showDialog(mContext, "The quantity of some products " +
//                                                    "you purchased seems to have changed. Please go back to verify.",
//                                            (dialog, which) -> {
//                                                SharedPreferences sharedPreferences = getSharedPreferences("CHECK", MODE_PRIVATE);
//                                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                                editor.putBoolean("CHECK_BUY_NOW", true);
//                                                editor.apply();
//                                            }, false);
//                                }
//                            }
//                        }

                        for (int i = 0; i < dataList.size(); i++) {
                            Product p = dataList.get(i);
                            Cart c = list.get(i);

                            if (p.getInventory() < c.getQuality()) {
                                check = false;
                                if (isOrder) {
                                    DialogUtils.showDialog(mContext, "The quantity of some products " +
                                                    "you purchased seems to have changed. Please go back to verify.",
                                            (dialog, which) -> {
                                                Intent intent = new Intent(mContext, CartActivity.class);
                                                intent.putExtra("ID_USER", ID_USER);
                                                startActivity(intent);
                                                finish();
                                            }, false);
                                } else {
                                    DialogUtils.showDialog(mContext, "The quantity of some products " +
                                                    "you purchased seems to have changed. Please go back to verify.",
                                            (dialog, which) -> {
                                                SharedPreferences sharedPreferences = getSharedPreferences("CHECK", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("BUY_NOW", true);
                                                editor.apply();
                                                finish();
                                            }, false);
                                }
                            }
                        }

                        if (check) {
                            DialogUtils.showDialog(mContext, mContext.getString(R.string.notifications),
                                    "Do you want to order?", "YES",
                                    (dialog, which) -> {
                                        FirebaseManager.addNewOrder(order, CheckoutActivity.this);
                                        clearText();
                                    },
                                    mContext.getString(R.string.no), (dialog, which) -> dialog.dismiss(), false);
                        }
                    }

                    @Override
                    public void onDataListError(String errorMessage) {

                    }
                });

            } else {
                DialogUtils.showDialog(mContext, "Please enter your information!");
            }
        });
    }

    private void getData() {
        ID_USER = getIntent().getStringExtra("ID_USER");
        ID_PRODUCT = getIntent().getStringExtra("ID_PRODUCT");
        QUALITY = getIntent().getLongExtra("QUALITY", -1);
        isOrder = (CommonActivity.isNullOrEmpty(ID_PRODUCT) && QUALITY == -1);

        if (isOrder) {
            list = cartDAO.getAllData();
        } else {
            list = new ArrayList<>();
            list.add(new Cart(ID_PRODUCT, QUALITY));
        }
        if (!list.isEmpty()) {
            List<String> idProducts = list.stream().map(Cart::getIdProduct).collect(Collectors.toList());
            FirebaseManager.getProductsByIds(idProducts, new OnGetDataByIdListener() {
                @Override
                public void onDataListLoaded(List<Product> dataList) {
                    products = dataList;

                    if (!list.isEmpty()) {
                        if (list.size() > 1) {
                            rcyProductUser.addItemDecoration(new DividerItemDecoration(mContext,
                                    DividerItemDecoration.VERTICAL));
                        }
                        adapter = new ProductCheckoutAdapter(mContext, list, dataList);
                        rcyProductUser.setAdapter(adapter);
                        calculator(list, dataList);
                    }
                }

                @Override
                public void onDataListError(String errorMessage) {
                    DialogUtils.showDialog(mContext, "Get data error. " + errorMessage);
                }
            });
        } else {
            DialogUtils.showDialog(mContext, "Your cart is nothing! Please add product to your cart.",
                    (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    }, false);
        }
    }

    private void setPopUp() {
        popUpConfirmInfo.setTranslationY(1500);
        tvInfo.setOnClickListener(v -> {
            showPopUp(0);
            tvChangeInfoSelect();
        });
        imgClosePopUp.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                clearText();
                tvInfoClear();
            }, 500);
            closePopUp(500);
        });
        edtPopUpAddressUser.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Intent intent = new Intent(mContext, ProvinceActivity.class);
                startActivity(intent);
            }
            return false;
        });
        btnConfirmInfoUser.setOnClickListener(v -> {
            Order order = validate(0);
            if (order != null) {
                new Handler().postDelayed(() -> {
                    tvNameUser.setText(order.getName());
                    tvPhoneUser.setText(order.getPhone());
                    tvAddressUser.setText(order.getAddress());
                    tvInfoClear();
                }, 500);
                closePopUp(500);
            }
        });
    }

    private Order validate(int type) {
        String name = edtPopUpNameUser.getText().toString().trim();
        String phone = edtPopUpPhoneUser.getText().toString().trim();
        String address = edtPopUpAddressUser.getText().toString().trim();

        if (CommonActivity.isNullOrEmpty(name)) {
            if (type == 0) {
                DialogUtils.showDialog(mContext, "Please enter your name");
            }
            return null;
        }
        if (CommonActivity.isNullOrEmpty(phone)) {
            if (type == 0) {
                DialogUtils.showDialog(mContext, "Please enter your phone");
            }
            return null;
        }
        if (!ValidateUtils.validatePhoneNumber(phone)) {
            if (type == 0) {
                DialogUtils.showDialog(mContext, "Please enter a valid phone number format.");
            }
            return null;
        }
        if (CommonActivity.isNullOrEmpty(address)) {
            if (type == 0) {
                DialogUtils.showDialog(mContext, "Please enter your address");
            }
            return null;
        }

        Order order = new Order();
        order.setIdUser(ID_USER);
        order.setName(name);
        order.setPhone(phone);
        order.setAddress(address);
        order.setTotal(total + 30000);
        order.setProducts(list);
        if (!CommonActivity.isNullOrEmpty(edtNoteUser.getText().toString().trim())) {
            order.setNote(edtNoteUser.getText().toString().trim());
        }

        return order;
    }

    private void calculator(List<Cart> carts, List<Product> products) {
        long quality = 0;
        total = 0;
        if (!carts.isEmpty() || !products.isEmpty()) {
            for (int i = 0; i < carts.size(); i++) {
                quality += list.get(i).getQuality();
                total += list.get(i).getQuality() * products.get(i).getPrice();
            }
        }
        tvTotalPriceUser.setText(CommonActivity.currencyFormat(total) + "đ");
        tvTotalCheckOut.setText(CommonActivity.currencyFormat(total + 30000) + "đ");
        tvQualityProductCheckOut.setText("( " + quality + " product )");

    }

    private void showPopUp(int timeDelay) {
        popUpConfirmInfo.animate().translationY(0).setDuration(500).setStartDelay(timeDelay).start();
    }

    public void closePopUp(int timeDelay) {
        popUpConfirmInfo.animate().translationY(1500).setDuration(500).setStartDelay(timeDelay).start();
    }

    private void tvChangeInfoSelect() {
        tvInfo.setBackgroundResource(R.drawable.tv_select_bg);
        tvInfo.setTextColor(Color.WHITE);
    }

    private void tvInfoClear() {
        tvInfo.setBackgroundResource(R.drawable.tv_bg);
        tvInfo.setTextColor(Color.parseColor("#283238"));
    }

    private void clearText() {
        edtPopUpNameUser.setText("");
        edtPopUpPhoneUser.setText("");
        edtPopUpAddressUser.setText("");

        tvNameUser.setText(null);
        tvPhoneUser.setText(null);
        tvAddressUser.setText(null);
    }

    @Override
    public void onWriteSuccess() {
        resultUpdate = 0;
        for (int i = 0; i < list.size(); i++) {
            Product p = products.get(i);
            Cart c = list.get(i);
            long inventory = p.getInventory() - c.getQuality();
            FirebaseManager.updateInventory(c.getIdProduct(), inventory, this);
        }
    }

    @Override
    public void onWriteFailure(String errorMessage) {
        DialogUtils.showDialog(mContext, "Order Fail. Please try again!");
    }

    @Override
    public void onUpdateSuccess() {
        ++resultUpdate;
        if (resultUpdate == list.size()) {
            if (isOrder) {
                int result = cartDAO.deleteAllData();
                if (result > 0) {
                    DialogUtils.showDialog(mContext, "Order Successfully", (dialog, which) -> {
                        Intent intent = new Intent(mContext, CartActivity.class);
                        intent.putExtra("ID_USER", ID_USER);
                        startActivity(intent);
                        finish();
                    }, false);
                }
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("CHECK", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("BUY_NOW", true);
                editor.apply();
                DialogUtils.showDialog(mContext, "Order Successfully", (dialog, which)
                        -> finish(), false);
            }
        }
    }

    @Override
    public void onUpdateFailure(String errorMessage) {

    }
}