package com.example.finalproject.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.finalproject.Firebase.FirebaseManager;
import com.example.finalproject.Models.OnUpdateDataListener;
import com.example.finalproject.Models.OnWriteDataListener;
import com.example.finalproject.Models.Product;
import com.example.finalproject.R;
import com.example.finalproject.Utils.CommonActivity;
import com.example.finalproject.Utils.DialogUtils;
import com.example.finalproject.Utils.ValidateUtils;
import com.google.android.material.textfield.TextInputEditText;

public class ProductEditorActivity extends AppCompatActivity {
    private Context mContext;
    private Intent intent;
    private Uri uriImage;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    private Product product;
    private boolean isAdd;

    private Toolbar toolBar;
    private ImageView img;
    private ImageView imgShow;
    private TextInputEditText edtName;
    private TextInputEditText edtPrice;
    private TextInputEditText edtInventory;
    private EditText edtDesc;
    private AppCompatButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_editor);

        initData();
    }

    private void initData() {
        FirebaseManager.init();
        mContext = ProductEditorActivity.this;

        toolBar = findViewById(R.id.tool_bar);
        img = findViewById(R.id.img);
        imgShow = findViewById(R.id.img_show);
        edtName = findViewById(R.id.edt_name);
        edtPrice = findViewById(R.id.edt_price);
        edtInventory = findViewById(R.id.edt_inventory);
        edtDesc = findViewById(R.id.edt_desc);
        btn = findViewById(R.id.btn);

        intent = getIntent();
        isAdd = intent.getBundleExtra("DATA") == null;

        String title;
        if (isAdd) {
            title = "Add Product";
            btn.setOnClickListener(v -> addProduct());
        }
        else {
            title = "Update Product";
            getData();
            setData();
            btn.setOnClickListener(v -> updateProduct());
        }

        img.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                uriImage = uri;
                imgShow.setImageURI(uri);
            }
            else {
                if (!isAdd) {
                    product.setImage(null);
                }
                uriImage = null;
                imgShow.setImageBitmap(null);
            }
        });

        setSupportActionBar(toolBar);
        toolBar.setTitle(title);
        toolBar.setNavigationOnClickListener(v -> {
            if (!isAdd) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ID_PRODUCT", product.getId());
                bundle.putString("NAME_PRODUCT", product.getName());
                bundle.putLong("PRICE_PRODUCT", product.getPrice());
                bundle.putString("DESC_PRODUCT", product.getDesc());
                bundle.putString("IMAGE_PRODUCT", product.getImage());
                bundle.putLong("INVENTORY_PRODUCT", product.getInventory());
                intent.putExtra("DATA", bundle);
                intent.putExtra("ADMIN", true);
                startActivity(intent);
            }
            finish();
        });
        btn.setText(title);
    }

    private void getData() {
        product = new Product();
        Bundle bundle = intent.getBundleExtra("DATA");
        product.setId(bundle.getString("ID_PRODUCT"));
        product.setName(bundle.getString("NAME_PRODUCT"));
        product.setPrice(bundle.getLong("PRICE_PRODUCT"));
        product.setDesc(bundle.getString("DESC_PRODUCT"));
        product.setImage(bundle.getString("IMAGE_PRODUCT"));
        product.setInventory(bundle.getLong("INVENTORY_PRODUCT"));
    }

    private void setData() {
        imgShow.setImageBitmap(CommonActivity.convertBase64ToBitmap(product.getImage()));
        edtName.setText(product.getName());
        edtPrice.setText(String.valueOf(product.getPrice()));
        edtInventory.setText(String.valueOf(product.getInventory()));
        edtDesc.setText(product.getDesc());
    }

    private void addProduct() {
        Product data = validate();
        if (!CommonActivity.isNullOrEmpty(data)) {
            FirebaseManager.addNewProduct(data, new OnWriteDataListener() {
                @Override
                public void onWriteSuccess() {
                    DialogUtils.showDialog(mContext, mContext.getString(R.string.notifications),
                            "Add Successfully", (dialog1, which1) -> finish(), false);
                }

                @Override
                public void onWriteFailure(String errorMessage) {

                }
            });
        }
    }

    private void updateProduct() {
        Product data = validate();
        if (!CommonActivity.isNullOrEmpty(data)) {
            data.setId(product.getId());
            FirebaseManager.updateProduct(data, new OnUpdateDataListener() {
                @Override
                public void onUpdateSuccess() {
                    DialogUtils.showDialog(mContext, mContext.getString(R.string.notifications),
                            "Update Successfully", (dialog1, which1) -> finish(), false);
                }

                @Override
                public void onUpdateFailure(String errorMessage) {
                    DialogUtils.showDialog(mContext, "Update Fail. " + errorMessage);
                }
            });
        }
    }

    private Product validate() {
        Product product;
        String name = edtName.getText().toString();
        String price = edtPrice.getText().toString();
        String inventory = edtInventory.getText().toString();
        String desc = edtDesc.getText().toString();

        if (CommonActivity.isNullOrEmpty(uriImage)) {
            if (isAdd) {
                DialogUtils.showDialog(mContext, "Please select your image product");
                return null;
            }
            else {
                if (CommonActivity.isNullOrEmpty(this.product.getImage())) {
                    DialogUtils.showDialog(mContext, "Please select your image product");
                    return null;
                }
            }
        }
        if (CommonActivity.isNullOrEmpty(name)) {
            DialogUtils.showDialog(mContext, "Please select your name product");
            return null;
        }
        if (ValidateUtils.isMinimumPasswords(name, 8)) {
            DialogUtils.showDialog(mContext, "Name too short, " +
                    "please enter minimum 8 characters!");
            return null;
        }
        if (CommonActivity.isNullOrEmpty(price)) {
            DialogUtils.showDialog(mContext, "Please select your price product");
            return null;
        }
        if (CommonActivity.isNullOrEmpty(inventory)) {
            DialogUtils.showDialog(mContext, "Please select your inventory product");
            return null;
        }
        if (CommonActivity.isNullOrEmpty(desc)) {
            DialogUtils.showDialog(mContext, "Please select your desc product");
            return null;
        }
        if (ValidateUtils.isMinimumPasswords(desc, 10)) {
            DialogUtils.showDialog(mContext, "Description too short, " +
                    "please enter minimum 10 characters!");
            return null;
        }

        product = new Product();
        product.setName(name);
        product.setPrice(Long.parseLong(price));
        product.setInventory(Long.parseLong(inventory));
        product.setDesc(desc);
        if (isAdd) {
            product.setImage(CommonActivity.convertUriToBase64(mContext, uriImage));
        } else {
            if (CommonActivity.isNullOrEmpty(uriImage)) {
                product.setImage(this.product.getImage());
            } else {
                product.setImage(CommonActivity.convertUriToBase64(mContext, uriImage));
            }
        }

        return product;
    }
}