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
 * RecyclerView adapter for the RecyclerView to display products
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    /**
     * List of products in the list
     */
    private List<Product> productList;

    /**
     * Android context
     */
    private Context context;

    /**
     * Constructor to create a new adapter
     * @param context Context given by Android
     */
    public ProductListAdapter(Context context) {
        this.context = context;
        productList = new ArrayList<Product>();
    }

    /**
     * Method to add a list of products into the adapter and <br/>
     * subsequently updates the associated {@link android.support.v7.widget.RecyclerView}
     * @param productList List of products to add
     */
    public void addItems(List<Product> productList) {
        this.productList.addAll(productList);
        notifyDataSetChanged();
    }

    /**
     * Method to replace the list of products in the adapter and <br/>
     * subsequently updates the associated {@link android.support.v7.widget.RecyclerView}
     * @param productList List of products to replace
     */
    public void setItems(List<Product> productList) {
        int originalSize = this.productList.size();
        this.productList.clear();
        this.productList.addAll(productList);
        notifyDataSetChanged();
    }

    /**
     * Method to obtain a product at a specified position in the associated {@link android.support.v7.widget.RecyclerView}
     * @param position Position of the product in the list
     * @return The product at the specified position
     */
    public Product getItem(int position) {
        return productList.get(position);
    }

    /**
     * Method to create a new viewholder - used to inflate the layout of the item
     * @param parent Parent view group
     * @param position Position of the item
     * @return The created viewholder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Method to bind data to the associated viewholder
     * @param viewHolder Viewholder created from {@link android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)}
     * @param position Position of the item
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Product product = productList.get(position);
        viewHolder.title.setText(product.getTitle());
        viewHolder.companyName.setText(product.getCompanyName());
        if (product.getImageUrl() != null ) {
            Picasso.with(context)
                    .load(product.getImageUrl())
                    .into(viewHolder.image);
        }
    }

    /**
     * Returns the total number of items in the list
     * @return Total number of items
     */
    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * Static class which implements the viewholder pattern
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * UI to show the product image
         */
        private final ImageView image;

        /**
         * UI to show the product title
         */
        private final TextView title;

        /**
         * UI to show the company name of the product
         */
        private final TextView companyName;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.product_image);
            title = (TextView) view.findViewById(R.id.product_title);
            companyName = (TextView) view.findViewById(R.id.company_name);
        }
    }
}
