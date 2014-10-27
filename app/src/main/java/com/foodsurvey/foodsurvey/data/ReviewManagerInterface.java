package com.foodsurvey.foodsurvey.data;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Interface for the review manager
 * Provides methods to submit and fetch reviews, and check if review exists
 */
public interface ReviewManagerInterface {

    /**
     * Asynchronous method to retrieve a list of reviews<br/>
     * Returns a list of {@link com.foodsurvey.foodsurvey.data.Review} objects
     *
     * @param offset Offset of the first review to fetch
     * @param limit Maximum number of reviews to fetch
     * @param productId ID of the product for the reviews
     * @param callback Callback to receive the result
     */
    public void getReviews(int offset, int limit, String productId, final ResultCallback<List> callback) ;

    /**
     * Asynchronous method to check if a review associated to a user and product exists<br/>
     * Returns true if exists and false if otherwise
     *
     * @param userId ID of the user
     * @param productId ID of the product
     * @param callback Callback to receive the result
     */
    public void checkIfReviewExists(String userId, String productId, final ResultCallback<Boolean> callback);

    /**
     * Asynchronous method to submit a review associated to a product and a user
     * Returns true if request is successful and false if otherwise
     *
     * @param data1 First data of the review
     * @param data2 Second data of the review
     * @param data3 Third data of the review
     * @param data4 Fourth data of the review
     * @param data5 Fifth data of the review (if any)
     * @param image Image path (on the device) of the review (if any)
     * @param userId ID of the user
     * @param productId ID of the product
     * @param callback Callback to receive the result
     */
    public void submitReview(String data1, String data2, String data3, String data4, @Nullable String data5, @Nullable String image, String userId, String productId, final ResultCallback<Boolean> callback);
}
