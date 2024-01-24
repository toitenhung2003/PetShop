package com.example.finalproject.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.finalproject.Database.CartDAO;
import com.example.finalproject.Firebase.FirebaseManager;
import com.example.finalproject.Models.Cart;
import com.example.finalproject.Models.OnDeleteDataListener;
import com.example.finalproject.Models.Product;
import com.example.finalproject.R;
import com.example.finalproject.Utils.CommonActivity;
import com.example.finalproject.Utils.CounterHandler;
import com.example.finalproject.Utils.DialogUtils;

public class ProductDetailActivity extends AppCompatActivity implements CounterHandler.CounterListener {
    private Context mContext;
    private Intent intent;

    private Product product;
    private boolean ADMIN;
    private long quality;
    private CartDAO cartDAO;
    private String ID_USER;

    private ImageView imgProduct;
    private ImageView imgCart;
    private TextView tvName;
    private TextView tvPrice;
    private ImageView imgIcInventory;
    private TextView tvInventory;
    private TextView tvDescription;
    private LinearLayout viewUser;
    private ConstraintLayout addToCart;
    private AppCompatButton btnBuyNow;
    private LinearLayout viewAdmin;
    private AppCompatButton btnUpdate;
    private AppCompatButton btnDelete;

    private CounterHandler.Builder builder;
    private ConstraintLayout popUp;
    private ImageView imgClose;
    private TextView tvNamePopUp;
    private ImageView imgPopUp;
    private TextView tvPricePopUp;
    private TextView augmentQuality;
    private TextView tvQualityPopUp;
    private TextView reduceQuality;
    private AppCompatButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("CHECK", MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("BUY_NOW", false);
        if (check) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("BUY_NOW");
            editor.apply();
            finish();
        }
    }

    private void initData() {
        mContext = ProductDetailActivity.this;
        cartDAO = new CartDAO(mContext);

        imgCart = findViewById(R.id.img_cart);
        imgProduct = findViewById(R.id.img_product);
        tvName = findViewById(R.id.tv_name);
        tvPrice = findViewById(R.id.tv_price);
        imgIcInventory = findViewById(R.id.img_ic_inventory);
        tvInventory = findViewById(R.id.tv_inventory);
        tvDescription = findViewById(R.id.tv_description);
        viewUser = findViewById(R.id.view_user);
        addToCart = findViewById(R.id.add_to_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        viewAdmin = findViewById(R.id.view_admin);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);

        popUp = findViewById(R.id.pop_up);
        imgClose = findViewById(R.id.img_close);
        tvNamePopUp = findViewById(R.id.tv_name_pop_up);
        imgPopUp = findViewById(R.id.img_pop_up);
        tvPricePopUp = findViewById(R.id.tv_price_pop_up);
        augmentQuality = findViewById(R.id.augment_quality);
        tvQualityPopUp = findViewById(R.id.tv_quality_pop_up);
        reduceQuality = findViewById(R.id.reduce_quality);
        btn = findViewById(R.id.btn);

        getData();
        setData();

    }

    private void getData() {
        intent = getIntent();
        product = new Product();
        ADMIN = intent.getBooleanExtra("ADMIN", false);

        Bundle bundle = intent.getBundleExtra("DATA");
        product.setId(bundle.getString("ID_PRODUCT"));
        product.setName(bundle.getString("NAME_PRODUCT"));
        product.setPrice(bundle.getLong("PRICE_PRODUCT"));
        product.setDesc(bundle.getString("DESC_PRODUCT"));
        product.setImage(bundle.getString("IMAGE_PRODUCT"));
        product.setInventory(bundle.getLong("INVENTORY_PRODUCT"));
    }

    private void setData() {
        if (ADMIN) {
            imgIcInventory.setVisibility(View.VISIBLE);
            tvInventory.setVisibility(View.VISIBLE);
            viewAdmin.setVisibility(View.VISIBLE);
            btnUpdate.setOnClickListener(v -> updateProduct());
            btnDelete.setOnClickListener(v -> deleteProduct());

            tvInventory.setText(String.valueOf(product.getInventory()));
        } else {
            ID_USER = intent.getStringExtra("ID_USER");
            imgCart.setVisibility(View.VISIBLE);
            viewUser.setVisibility(View.VISIBLE);
            imgCart.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, CartActivity.class);
                intent.putExtra("ID_USER", ID_USER);
                startActivity(intent);
            });
            addToCart.setOnClickListener(v -> {
                builder.build();
                btn.setText("Add to cart");
                btn.setOnClickListener(v1 -> addProductToCart());
                showPopUp(0);
            });
            btnBuyNow.setOnClickListener(v -> {
                builder.build();
                btn.setText("Buy now");
                btn.setOnClickListener(v1 -> buyNow());
                showPopUp(0);
            });

            setPopUp();
            imgPopUp.setImageBitmap(CommonActivity.convertBase64ToBitmap(product.getImage()));
            tvNamePopUp.setText(product.getName());
            tvPricePopUp.setText(CommonActivity.currencyFormat(product.getPrice()) + "đ");
            builder = new CounterHandler.Builder()
                    .incrementalView(augmentQuality)
                    .decrementalView(reduceQuality)
                    .startNumber(1)
                    .minRange(1) // cant go any less than -50
                    .maxRange(product.getInventory() + 1) // cant go any further than 50
                    .isCycle(false) // 49,50,-50,-49 and so on
                    .counterDelay(100) // speed of counter
                    .counterStep(1)  // steps e.g. 0,2,4,6...
                    .listener(this) // to listen counter results and show them in app
            ;
        }

        imgProduct.setImageBitmap(CommonActivity.convertBase64ToBitmap(product.getImage()));
        tvName.setText(product.getName());
        tvPrice.setText(CommonActivity.currencyFormat(product.getPrice()) + "đ");
        tvDescription.setText(product.getDesc());
    }

    private void setPopUp() {
        popUp.setVisibility(View.VISIBLE);
        popUp.setTranslationY(1500);
        imgClose.setOnClickListener(v -> closePopUp(0));
    }

    private void showPopUp(int timeDelay) {
        popUp.animate().translationY(0).setStartDelay(timeDelay).setDuration(500).start();
    }

    private void closePopUp(int timeDelay) {
        popUp.animate().translationY(1500).setStartDelay(timeDelay).setDuration(500).start();
    }
    
    private void addProductToCart() {
        int check = cartDAO.checkProduct(product.getId(), quality);
        if (check > 0) {
            finish();
            Toast.makeText(mContext, "Update quality product successfully", Toast.LENGTH_SHORT).show();
        }
        else {
            long result = cartDAO.insert(new Cart(product.getId(), quality));
            if (result > 0) {
                finish();
                Toast.makeText(mContext, "Add to cart successfully", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(mContext, "Add fail. Please trail again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateProduct() {
        Intent intent = new Intent(mContext, ProductEditorActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID_PRODUCT", product.getId());
        bundle.putString("NAME_PRODUCT", product.getName());
        bundle.putLong("PRICE_PRODUCT", product.getPrice());
        bundle.putString("DESC_PRODUCT", product.getDesc());
        bundle.putString("IMAGE_PRODUCT", product.getImage());
        bundle.putLong("INVENTORY_PRODUCT", product.getInventory());
        intent.putExtra("DATA", bundle);
        startActivity(intent);
        finish();
    }

    private void deleteProduct() {
        DialogUtils.showDialog(mContext, "Delete Product?",
                "Do you want to delete product have name: " + product.getName() + " ?", (dialog, which)
                        -> FirebaseManager.deleteProduct(product.getId(), new OnDeleteDataListener() {
                    @Override
                    public void onDeleteSuccess() {
                        DialogUtils.showDialog(mContext, mContext.getString(R.string.notifications), "Delete Successfully",
                                (dialog1, which1) -> finish(), false);
                    }

                    @Override
                    public void onDeleteFailure(String errorMessage) {
                        DialogUtils.showDialog(mContext, "Delete Fail. " + errorMessage);
                    }
                }), false);
    }

    private void buyNow() {
        Intent intent = new Intent(mContext, CheckoutActivity.class);
        intent.putExtra("ID_USER", ID_USER);
        intent.putExtra("ID_PRODUCT", product.getId());
        intent.putExtra("QUALITY", quality);
        startActivity(intent);
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    public void onIncrement(View view, long number) {
        if (number > product.getInventory()) {
            --number;
            DialogUtils.showDialog(mContext, "Only " + product.getInventory() + " products left",
                    (dialog, which) -> dialog.dismiss(), false);
        }
        quality = number;
        tvQualityPopUp.setText(String.valueOf(number));
    }

    @Override
    public void onDecrement(View view, long number) {
        quality = number;
        tvQualityPopUp.setText(String.valueOf(number));
    }
}