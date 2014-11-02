package com.foodsurvey.foodsurvey.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.foodsurvey.foodsurvey.DbConstants;
import com.foodsurvey.foodsurvey.entity.Review;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the review manager
 * Allows for submitting and fetching reviews, and checking if review exists
 *
 * @author Hee Jun Yi
 */
public class ReviewManager implements ReviewManagerInterface {

    public void getReviews(int offset, int limit, String productId, final ResultCallback<List> callback) {
        FetchReviewsTask task = new FetchReviewsTask(productId) {
            @Override
            protected void onPostExecute(List result) {
                super.onPostExecute(result);
                if (callback != null)
                    callback.onResult(result);
            }
        };

        task.execute(new Integer[]{
                offset, limit
        });
    }

    public void checkIfReviewExists(String userId, String productId, final ResultCallback<String> callback) {
        CheckReviewExistsTask task = new CheckReviewExistsTask() {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (callback != null)
                    callback.onResult(result);
            }
        };

        task.execute(new String[]{
                userId, productId
        });
    }

    public void submitReview(String data1, String data2, String data3, String data4, String data5, String image, String userId, String productId, final ResultCallback<Boolean> callback) {
        SubmitReviewTask task = new SubmitReviewTask() {
            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (callback != null)
                    callback.onResult(result);
            }
        };

        task.execute(new String[]{
                data1, data2, data3, data4, data5, image, userId, productId
        });
    }

    /**
     * AsyncTask to check if review exists
     */
    private class CheckReviewExistsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];
            String productId = params[1];

            try {
                ParseQuery<ParseObject> reviewQuery = ParseQuery.getQuery(DbConstants.TABLE_REVIEW);
                reviewQuery.whereEqualTo(DbConstants.REVIEW_PRODUCT_ID, ParseObject.createWithoutData(DbConstants.TABLE_PRODUCT, productId));
                reviewQuery.whereEqualTo(DbConstants.USER_ID, ParseObject.createWithoutData(DbConstants.TABLE_USER, userId));
                reviewQuery.setLimit(1);
                List<ParseObject> results = reviewQuery.find();
                if (results != null && results.size() > 0)
                    return results.get(0).getObjectId();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Async task to fetch reviews
     */
    private class FetchReviewsTask extends AsyncTask<Integer, Void, List> {
        String productId;

        public FetchReviewsTask(String productId) {
            this.productId = productId;
        }

        @Override
        protected List doInBackground(Integer... integers) {
            int offset = integers[0];
            int limit = integers[1];

            try {

                ParseQuery<ParseObject> reviewQuery = ParseQuery.getQuery(DbConstants.TABLE_REVIEW);

                // Get the most recent ones
                reviewQuery.orderByDescending(DbConstants.CREATED_AT);

                reviewQuery.include(DbConstants.REVIEW_USER_ID);

                reviewQuery.setSkip(offset);

                reviewQuery.setLimit(limit);

                reviewQuery.whereEqualTo(DbConstants.REVIEW_PRODUCT_ID, ParseObject.createWithoutData(DbConstants.TABLE_PRODUCT, productId));

                List<Review> reviewList = new ArrayList<Review>();
                List<ParseObject> result = reviewQuery.find();
                for (ParseObject reviewObject : result) {
                    Review review = new Review();
                    review.setId(reviewObject.getObjectId());
                    review.setData1(reviewObject.getString(DbConstants.REVIEW_DATA1));
                    review.setData2(reviewObject.getString(DbConstants.REVIEW_DATA2));
                    review.setData3(reviewObject.getString(DbConstants.REVIEW_DATA3));
                    review.setData4(reviewObject.getString(DbConstants.REVIEW_DATA4));
                    review.setData5(reviewObject.getString(DbConstants.REVIEW_DATA5));
                    review.setImageUrl(reviewObject.getString(DbConstants.REVIEW_IMAGE));
                    review.setProductId(productId);
                    review.setAgeGroup(reviewObject.getParseObject(DbConstants.REVIEW_USER_ID).getString(DbConstants.USER_AGE_GROUP));
                    reviewList.add(review);
                }
                return reviewList;

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * AsyncTask to submit review
     */
    private class SubmitReviewTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String data1 = params[0];
            String data2 = params[1];
            String data3 = params[2];
            String data4 = params[3];
            String data5 = params[4];
            String image = params[5];
            String userId = params[6];
            String productId = params[7];

            try {
                ParseObject reviewObject = new ParseObject("Review");
                reviewObject.put(DbConstants.REVIEW_DATA1, data1);
                reviewObject.put(DbConstants.REVIEW_DATA2, data2);
                reviewObject.put(DbConstants.REVIEW_DATA3, data3);
                reviewObject.put(DbConstants.REVIEW_DATA4, data4);
                reviewObject.put(DbConstants.REVIEW_DATA5, data5);
                reviewObject.put(DbConstants.REVIEW_PRODUCT_ID, ParseObject.createWithoutData(DbConstants.TABLE_PRODUCT, productId));
                reviewObject.put(DbConstants.REVIEW_USER_ID, ParseObject.createWithoutData(DbConstants.TABLE_USER, userId));

                Bitmap bitmap = BitmapFactory.decodeFile(image);

                if (bitmap != null) {
                    // Convert it to byte
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] imageBytes = stream.toByteArray();

                    // Create the ParseFile
                    ParseFile file = new ParseFile("product_image.jpg", imageBytes);
                    // Upload the image into Parse Cloud
                    file.save();

                    String imageUrl = file.getUrl();
                    reviewObject.put("image", imageUrl);

                    File localFile = new File(image);
                    String directory = localFile.getParentFile().getName();
                    if (directory.equals("myfolder") && localFile.exists()) {
                        localFile.delete();
                    }
                }
                reviewObject.save();
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
