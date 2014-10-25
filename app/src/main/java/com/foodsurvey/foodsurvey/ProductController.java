package com.foodsurvey.foodsurvey;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dashsell on 30/9/14.
 */
public class ProductController {
    private static final String TABLE_PRODUCT = "Product";
    private static final String PRODUCT_TITLE = "title";
    private static final String PRODUCT_DESCRIPTION = "description";
    private static final String PRODUCT_PACKAGE_TYPE = "packageType";
    private static final String PRODUCT_IMAGE = "image";

    private static ProductController instance = null;

    public static ProductController getInstance() {
        if (instance == null)
            instance = new ProductController();
        return instance;
    }

    public void createProduct(String companyId, String title, String description, String packageType, String imagePath, final ResultCallback<Boolean> callback) {
        CreateProductTask task = new CreateProductTask() {
            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (callback != null) {
                    callback.onResult(result);
                }
            }
        };

        task.execute(new String[]{
                companyId, title, description, packageType, imagePath
        });
    }

    public void getProducts(int offset, int limit, String companyId, final ResultCallback<List> callback) {
        FetchProductsTask task = new FetchProductsTask(companyId) {
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


    public void updateProduct(String id, String title, String description, String packageType, String imagePath, final ResultCallback<Boolean> callback) {
        UpdateProductTask task = new UpdateProductTask() {
            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (callback != null)
                    callback.onResult(result);
            }
        };

        task.execute(new String[]{
                id, title, description, packageType, imagePath
        });

    }

    class FetchProductsTask extends AsyncTask<Integer, Void, List> {
        String companyId;

        public FetchProductsTask(String companyId) {
            this.companyId = companyId;
        }

        @Override
        protected List doInBackground(Integer... integers) {
            int offset = integers[0];
            int limit = integers[1];

            try {
                ParseQuery<ParseObject> productQuery = ParseQuery.getQuery(TABLE_PRODUCT);

                // Get the most recent ones
                productQuery.orderByDescending("createdAt");

                productQuery.include("companyId");

                if (companyId != null)
                    productQuery.whereEqualTo("companyId", ParseObject.createWithoutData("Company", companyId));

                productQuery.setSkip(offset);

                productQuery.setLimit(limit);

                List<Product> productList = new ArrayList<Product>();
                List<ParseObject> result = productQuery.find();
                for (ParseObject productObject : result) {
                    Product product = new Product();
                    product.setId(productObject.getObjectId());
                    product.setTitle(productObject.getString(PRODUCT_TITLE));
                    product.setDescription(productObject.getString(PRODUCT_DESCRIPTION));
                    product.setPackageType(productObject.getString(PRODUCT_PACKAGE_TYPE));
                    product.setCompanyName(productObject.getParseObject("companyId").getString("name"));
                    product.setImageUrl(productObject.getString(PRODUCT_IMAGE));
                    productList.add(product);
                }

                return productList;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    private class UpdateProductTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {

                String id = params[0];
                String title = params[1];
                String description = params[2];
                String packageType = params[3];
                String imagePath = params[4];

                ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_PRODUCT);
                ParseObject productObject = query.get(id);
                productObject.put(PRODUCT_TITLE, title);
                productObject.put(PRODUCT_DESCRIPTION, description);
                productObject.put(PRODUCT_PACKAGE_TYPE, packageType);
                productObject.put(PRODUCT_IMAGE, imagePath);

                productObject.save();

                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private class CreateProductTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String companyId = params[0];
            String title = params[1];
            String description = params[2];
            String packageType = params[3];
            String imagePath = params[4];

            try {
                ParseObject productObject = new ParseObject(TABLE_PRODUCT);
                productObject.put("companyId", ParseObject.createWithoutData("Company", companyId));
                productObject.put(PRODUCT_TITLE, title);
                productObject.put(PRODUCT_DESCRIPTION, description);
                productObject.put(PRODUCT_PACKAGE_TYPE, packageType);
                productObject.save();

                String productId = productObject.getObjectId();

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                // Convert it to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();

                // Create the ParseFile
                ParseFile file = new ParseFile("product_image.jpg", image);
                // Upload the image into Parse Cloud
                file.save();

                String imageUrl = file.getUrl();
                productObject.add(PRODUCT_IMAGE, imageUrl);
                productObject.save();

            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

}
