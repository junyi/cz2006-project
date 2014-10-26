package com.foodsurvey.foodsurvey.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.Review;

import java.util.ArrayList;
import java.util.List;

public class AdminReviewListAdapter extends RecyclerView.Adapter<AdminReviewListAdapter.ViewHolder> {
    private List<Review> reviewList;
    private Context context;

    public AdminReviewListAdapter(Context context) {
        this.context = context;
        reviewList = new ArrayList<Review>();
    }

    public void addItems(List<Review> reviewList) {
        int originalSize = this.reviewList.size();
        this.reviewList.addAll(reviewList);
//        notifyItemRangeInserted(originalSize, reviewList.size());
        notifyDataSetChanged();
    }

    public void setItems(List<Review> reviewList) {
        int originalSize = this.reviewList.size();
        this.reviewList.clear();
        this.reviewList.addAll(reviewList);
//        notifyItemRangeInserted(originalSize, reviewList.size());
        notifyDataSetChanged();
    }

    public Review getItem(int position) {
        return reviewList.get(position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Review review = reviewList.get(position);
        int score1 = Integer.parseInt(review.getData1());
        int score2 = Integer.parseInt(review.getData2());
        int score3 = Integer.parseInt(review.getData3());
        int score4 = Integer.parseInt(review.getData4());

        System.out.printf("1:%s,2:%s,3:%s,4:%s\n", score1, score2, score3, score4);
        viewHolder.q1Progress.setProgress(score1);
        viewHolder.q2Progress.setProgress(score2);
        viewHolder.q3Progress.setProgress(score3);
        viewHolder.q4Progress.setProgress(score4);

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar q1Progress;
        private ProgressBar q2Progress;
        private ProgressBar q3Progress;
        private ProgressBar q4Progress;


        public ViewHolder(View view) {
            super(view);
            q1Progress = (ProgressBar) view.findViewById(R.id.q1_score);
            q2Progress = (ProgressBar) view.findViewById(R.id.q2_score);
            q3Progress = (ProgressBar) view.findViewById(R.id.q3_score);
            q4Progress = (ProgressBar) view.findViewById(R.id.q4_score);

        }
    }
}
