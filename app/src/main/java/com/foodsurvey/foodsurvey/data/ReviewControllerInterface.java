package com.foodsurvey.foodsurvey.data;

import java.util.List;

public interface ReviewControllerInterface {

    public void getReviews(int offset, int limit, String productId, final ResultCallback<List> callback) ;

    public void checkIfReviewExists(String userId, String productId, final ResultCallback<Boolean> callback);

    public void submitReview(String[] params, final ResultCallback<Boolean> callback);

    public void submitReview(String data1, String data2, String data3, String data4, String data5, String image, String userId, String productId, final ResultCallback<Boolean> callback);
}
