package com.foodsurvey.foodsurvey.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.foodsurvey.foodsurvey.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dashsell on 1/10/14.
 */
public class ProductImageSliderAdapter extends RecyclerView.Adapter<ProductImageSliderAdapter.ViewHolder> {
    private List<String> images;
    private Context context;

    public ProductImageSliderAdapter(Context context) {
        images = new ArrayList<String>();
        this.context = context;
    }

    public void addImages(List<String> images) {
        int originalSize = this.images.size();
        this.images.addAll(images);
//        notifyItemRangeInserted(originalSize, images.size());
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_image_big, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Picasso.with(context)
                .load(images.get(position))
                .into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.product_image);
        }
    }
}
