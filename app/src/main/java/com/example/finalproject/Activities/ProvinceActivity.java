package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Adapters.ProvinceAdapter;
import com.example.finalproject.Models.MainContentProvinceModel;
import com.example.finalproject.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProvinceActivity extends AppCompatActivity {
    public String PROVINCE = null;
    public String CITY = null;
    public String ADDRESS = null;

    public TextView tvUserAddressAtv;
    private EditText edtAddressUserAtv;
    private ExpandableListView expandListProvince;
    private AppCompatButton btnGetAddressUserAtv;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province);

        tvUserAddressAtv = (TextView) findViewById(R.id.tv_user_address_atv);
        edtAddressUserAtv = (EditText) findViewById(R.id.edt_address_user_atv);
        expandListProvince = (ExpandableListView) findViewById(R.id.expand_list_province);
        btnGetAddressUserAtv = (AppCompatButton) findViewById(R.id.btn_get_address_user_atv);

        sharedPreferences = getSharedPreferences("PROVINCES", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        new LoadContentAsync().execute();

        edtAddressUserAtv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvUserAddressAtv.setText(charSequence + ", " + (CITY == null ? "" : CITY) + ", " + (PROVINCE == null ? "" : PROVINCE));
                ADDRESS = String.valueOf(charSequence);
                if (edtAddressUserAtv.getText().length() == 0) {
                    ADDRESS = null;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        expandListProvince.setOnGroupClickListener((expandableListView, view, i, l) -> {
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            String province = tvName.getText().toString();

            CITY = null;
            PROVINCE = province;
            if (ADDRESS == null) {
                tvUserAddressAtv.setText(", " + PROVINCE);
            } else {
                tvUserAddressAtv.setText(ADDRESS + ", " + PROVINCE);
            }

            return false;
        });
        expandListProvince.setOnChildClickListener((expandableListView, view, i, i1, l) -> {
            TextView tvName = (TextView) view.findViewById(R.id.tv_cities);
            String city = tvName.getText().toString();

            CITY = city;
            if (ADDRESS == null) {
                tvUserAddressAtv.setText(", " + CITY + ", " + PROVINCE);
            } else {
                tvUserAddressAtv.setText(ADDRESS + ", " + CITY + ", " + PROVINCE);
            }

            return false;
        });

        btnGetAddressUserAtv.setOnClickListener(v -> {
            validate();
            if (PROVINCE != null && CITY != null && ADDRESS != null) {
                editor.putString("DATA_PROVINCE" ,ADDRESS  + ", " + CITY + ", " + PROVINCE);
                editor.apply();

                finish();
            }
        });
    }

    private void validate() {
        if (PROVINCE == null) {
            Toast.makeText(this, "Vui lòng chọn Tỉnh/Thành phố", Toast.LENGTH_SHORT).show();
            return;
        }
        if (CITY == null) {
            Toast.makeText(this, "Vui lòng chọn Quận/Huyện", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ADDRESS == null) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ cụ thể", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void setDefaultAddress() {
        PROVINCE = null;
        CITY = null;
        ADDRESS = null;
    }

    public void goToBackActivity(View view) {
        editor.remove("DATA_PROVINCE");
        editor.apply();
        finish();
    }

    class LoadContentAsync extends AsyncTask<Void, Void, MainContentProvinceModel> {

        @Override
        protected MainContentProvinceModel doInBackground(Void... voids) {
            Gson gson = new Gson();
            MainContentProvinceModel mainContentModel = null;

            try {
                InputStream is = getAssets().open("provinces.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                synchronized (this) {
                    mainContentModel = gson.fromJson(reader, MainContentProvinceModel.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mainContentModel;
        }

        @Override
        protected void onPostExecute(MainContentProvinceModel mainContentModel) {
            super.onPostExecute(mainContentModel);

            ProvinceAdapter phoneListAdapter = new ProvinceAdapter(ProvinceActivity.this, mainContentModel.getProvinces());
            expandListProvince.setAdapter(phoneListAdapter);
        }
    }
}