package com.example.finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Models.Cart;
import com.example.finalproject.Models.Product;
import com.example.finalproject.R;
import com.example.finalproject.Utils.CommonActivity;

import java.util.List;

public class ProductCheckoutAdapter extends RecyclerView.Adapter<ProductCheckoutAdapter.ProductCheckoutViewHolder>{
    private Context mContext;
    private List<Cart> listCart;
    private List<Product> listProduct;

    public ProductCheckoutAdapter(Context mContext, List<Cart> listCart, List<Product> listProduct) {
        this.mContext = mContext;
        this.listCart = listCart;
        this.listProduct = listProduct;
    }

    @NonNull
    @Override
    public ProductCheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_checkout_product, parent, false);
        return new ProductCheckoutViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCheckoutViewHolder holder, int position) {
        Product product = listProduct.get(position);
        Cart cart = listCart.get(position);

        holder.imgProduct.setImageBitmap(CommonActivity.convertBase64ToBitmap(product.getImage()));
        holder.tvNameProduct.setText(CommonActivity.truncateString(product.getName(), 50));
        holder.tvPriceProduct.setText(CommonActivity.currencyFormat(product.getPrice()) + "đ");
        holder.tvQualityProduct.setText(String.valueOf(cart.getQuality()));
    }

    @Override
    public int getItemCount() {
        return (listCart.isEmpty() && listProduct.isEmpty()) ? 0 : listCart.size();
    }

    public static class ProductCheckoutViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct;
        private TextView tvNameProduct;
        private TextView tvPriceProduct;
        private TextView tvQualityProduct;

        public ProductCheckoutViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            tvNameProduct = (TextView) itemView.findViewById(R.id.tv_name_product);
            tvPriceProduct = (TextView) itemView.findViewById(R.id.tv_price_product);
            tvQualityProduct = (TextView) itemView.findViewById(R.id.tv_quality_product);
        }
    }
}
