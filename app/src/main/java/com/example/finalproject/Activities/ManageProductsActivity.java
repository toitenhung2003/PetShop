package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.Adapters.ProductAdapter;
import com.example.finalproject.Firebase.FirebaseManager;
import com.example.finalproject.Models.OnGetDataListener;
import com.example.finalproject.Models.Product;
import com.example.finalproject.R;
import com.example.finalproject.Utils.DialogUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ManageProductsActivity extends AppCompatActivity implements OnGetDataListener {
    private Context mContext;

    private boolean ADMIN;
    private String ID_USER;

    private ProductAdapter adapter;
    private TextView title;
    private EditText edtSearch;
    private ImageView imgSearch;
    private RecyclerView rcyProduct;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseManager.getAllData(this);
    }

    private void initData() {
        FirebaseManager.init();
        FirebaseManager.getAllData(this);
        Intent intent = getIntent();
        ADMIN = intent.getBooleanExtra("ADMIN", false);
        mContext = ManageProductsActivity.this;

        title = findViewById(R.id.title);
        edtSearch = findViewById(R.id.edt_search);
        imgSearch = findViewById(R.id.img_search);
        rcyProduct = findViewById(R.id.rcy_product);
        fab = findViewById(R.id.fab);

        rcyProduct.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        imgSearch.setOnClickListener(v -> adapter.onSearch(edtSearch.getText().toString()));

        if (ADMIN) {
            fab.setForeground(AppCompatResources.getDrawable(mContext, R.drawable.ic_add_product));
            fab.setOnClickListener(v -> startActivity(new Intent(mContext, ProductEditorActivity.class)));
        }
        else {
            ID_USER = intent.getStringExtra("ID_USER");
            fab.setForeground(AppCompatResources.getDrawable(mContext, R.drawable.cart));
            fab.setOnClickListener(v -> {
                Intent intent1 = new Intent(mContext, CartActivity.class);
                intent1.putExtra("ID_USER", ID_USER);
                startActivity(intent1);
            });
        }
    }

    @Override
    public void onDataListLoaded(List<Product> dataList) {
        adapter = new ProductAdapter(mContext, this, dataList, ADMIN, ID_USER);
        rcyProduct.setAdapter(adapter);
    }

    @Override
    public void onDataListError(String errorMessage) {
        DialogUtils.showDialog(mContext, "Get data error. " + errorMessage);
    }
}