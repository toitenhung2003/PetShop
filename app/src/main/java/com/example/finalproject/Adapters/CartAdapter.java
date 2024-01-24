package com.example.finalproject.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Database.CartDAO;
import com.example.finalproject.Models.Cart;
import com.example.finalproject.Models.Product;
import com.example.finalproject.R;
import com.example.finalproject.Utils.CommonActivity;
import com.example.finalproject.Utils.CounterHandler;
import com.example.finalproject.Utils.DialogUtils;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context mContext;
    private CartDAO dao;
    private List<Cart> listCart;
    private List<Product> listProduct;
    private OnChangeQualityListener listener;

    public CartAdapter(Context context, List<Cart> listCart, List<Product> listProduct, OnChangeQualityListener listener) {
        this.mContext = context;
        this.listCart = listCart;
        this.listProduct = listProduct;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        dao = new CartDAO(mContext);
        return new CartViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = listProduct.get(position);
        Cart cart = listCart.get(position);

        if (product.getInventory() != 0 && cart.getQuality() != 0) {
            holder.img.setImageBitmap(CommonActivity.convertBase64ToBitmap(product.getImage()));
            holder.tvName.setText(CommonActivity.truncateString(product.getName(), 30));
            holder.tvPrice.setText(CommonActivity.currencyFormat(product.getPrice()) + "đ");
            holder.tvQuality.setText(String.valueOf(cart.getQuality()));

            CounterHandler.Builder builder = new CounterHandler.Builder()
                    .incrementalView(holder.augmentQuality)
                    .decrementalView(holder.reduceQuality)
                    .startNumber(cart.getQuality())
                    .minRange(0) // cant go any less than -50
                    .maxRange(product.getInventory() + 1) // cant go any further than 50
                    .isCycle(false) // 49,50,-50,-49 and so on
                    .counterDelay(100) // speed of counter
                    .counterStep(1)  // steps e.g. 0,2,4,6...
                    .listener(new CounterHandler.CounterListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onIncrement(View view, long number) {
                            if (number > product.getInventory()) {
                                --number;
                                DialogUtils.showDialog(mContext, "Only " + product.getInventory()
                                        + " products left", (dialog, which) -> dialog.dismiss(), false);
                            } else {
                                int result = dao.update(cart.getIdProduct(), number);
                                if (result > 0) {
                                    cart.setQuality(number);
                                    listCart.set(position, cart);
                                    listener.onChangeQuality(listCart, listProduct);
                                    holder.tvQuality.setText(String.valueOf(number));
                                }
                            }
                        }

                        @Override
                        public void onDecrement(View view, long number) {
                            if (number == 0) {
                                int result = dao.delete(cart.getIdProduct());
                                if (result > 0) {
                                    listCart.remove(cart);
                                    listProduct.remove(product);
                                    listener.onChangeQuality(listCart, listProduct);
                                    notifyItemRemoved(position);
                                    new Handler().postDelayed(() -> notifyDataSetChanged(), 300);
                                }
                            } else {
                                int result = dao.update(cart.getIdProduct(), number);
                                if (result > 0) {
                                    cart.setQuality(number);
                                    listCart.set(position, cart);
                                    listener.onChangeQuality(listCart, listProduct);
                                    holder.tvQuality.setText(String.valueOf(number));
                                }
                            }
                        }
                    }) // to listen counter results and show them in app
                    ;
            builder.build();
        } else {
            int result = dao.delete(cart.getIdProduct());
            if (result > 0) {
                listCart.remove(cart);
                listProduct.remove(product);
                listener.onChangeQuality(listCart, listProduct);
                notifyDataSetChanged();
                DialogUtils.showDialog(mContext, "We have just updated the quantity in your " +
                        "cart as the remaining quantity of the product has changed");

            }
        }
    }

    @Override
    public int getItemCount() {
        return (listCart.isEmpty() || listProduct.isEmpty()) ? 0 : listProduct.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView tvName;
        private TextView tvPrice;
        private TextView augmentQuality;
        private TextView tvQuality;
        private TextView reduceQuality;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.img);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            augmentQuality = (TextView) itemView.findViewById(R.id.augment_quality);
            tvQuality = (TextView) itemView.findViewById(R.id.tv_quality);
            reduceQuality = (TextView) itemView.findViewById(R.id.reduce_quality);
        }
    }
}
