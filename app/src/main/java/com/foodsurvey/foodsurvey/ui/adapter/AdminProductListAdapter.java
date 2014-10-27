package com.foodsurvey.foodsurvey.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodsurvey.foodsurvey.data.Product;
import com.foodsurvey.foodsurvey.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AdminProductListAdapter extends RecyclerView.Adapter<AdminProductListAdapter.ViewHolder> {
    private List<Product> productList;
    private Context context;

    public AdminProductListAdapter(Context context) {
        this.context = context;
        productList = new ArrayList<Product>();
    }

    public void addItems(List<Product> productList) {
        int originalSize = this.productList.size();
        this.productList.addAll(productList);
//        notifyItemRangeInserted(originalSize, productList.size());
        notifyDataSetChanged();
    }

    public void setItems(List<Product> productList) {
        int originalSize = this.productList.size();
        this.productList.clear();
        this.productList.addAll(productList);
//        notifyItemRangeInserted(originalSize, productList.size());
        notifyDataSetChanged();
    }

    public Product getItem(int position) {
        return productList.get(position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Product product = productList.get(position);
        viewHolder.title.setText(product.getTitle());
        viewHolder.companyName.setText(product.getCompanyName());
        if (product.getImageUrl() != null) {
            Picasso.with(context)
                    .load(product.getImageUrl())
                    .into(viewHolder.image);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView title;
        private final TextView companyName;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.product_image);
            title = (TextView) view.findViewById(R.id.product_title);
            companyName = (TextView) view.findViewById(R.id.company_name);
        }
    }
}
