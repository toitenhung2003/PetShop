package com.example.finalproject.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Activities.ProductDetailActivity;
import com.example.finalproject.Firebase.FirebaseManager;
import com.example.finalproject.Models.OnDeleteDataListener;
import com.example.finalproject.Models.OnGetDataListener;
import com.example.finalproject.Models.OnUpdateDataListener;
import com.example.finalproject.Models.Product;
import com.example.finalproject.R;
import com.example.finalproject.Utils.CommonActivity;
import com.example.finalproject.Utils.DialogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context mContext;
    private OnGetDataListener listener;
    private List<Product> list;
    private List<Product> listTmp;

    private final boolean ADMIN;
    private final String ID_USER;

    public ProductAdapter(Context mContext, OnGetDataListener listener, List<Product> list, boolean isAdmin, String ID_USER) {
        this.mContext = mContext;
        this.listener = listener;
        this.list = list;
        listTmp = Collections.unmodifiableList(new ArrayList<>(list));
        this.ADMIN = isAdmin;
        this.ID_USER = ID_USER;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FirebaseManager.init();
        View item_view = LayoutInflater.from(mContext).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = list.get(position);

        holder.tvName.setText(CommonActivity.truncateString(product.getName(), 30));
        holder.tvPrice.setText(CommonActivity.currencyFormat(product.getPrice()) + "đ");
        holder.tvDesc.setText(CommonActivity.truncateString(product.getDesc(), 65));
        holder.imgProduct.setImageBitmap(CommonActivity.convertBase64ToBitmap(product.getImage()));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ProductDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ID_PRODUCT", product.getId());
            bundle.putString("NAME_PRODUCT", product.getName());
            bundle.putLong("PRICE_PRODUCT", product.getPrice());
            bundle.putString("DESC_PRODUCT", product.getDesc());
            bundle.putString("IMAGE_PRODUCT", product.getImage());
            bundle.putLong("INVENTORY_PRODUCT", product.getInventory());
            intent.putExtra("DATA", bundle);
            intent.putExtra("ADMIN", ADMIN);
            intent.putExtra("ID_USER", ID_USER);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvDesc;
        private ImageView imgProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
        }
    }

    public void reloadData(List<Product> listSearch) {
        list.clear();
        list.addAll(listSearch);
        notifyDataSetChanged();
    }

    public void onSearch(String product) {
        if (product.isEmpty()) {
            reloadData(listTmp);
        } else {
            reloadData(listTmp.stream()
                    .filter(item -> item.getName().toLowerCase().contains(product.trim().toLowerCase()))
                    .collect(Collectors.toList()));
        }
    }
}
