package com.foodsurvey.foodsurvey.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.foodsurvey.foodsurvey.DbConstants;
import com.foodsurvey.foodsurvey.entity.Product;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of product manager using Parse
 * Allows for creating, fetching and updating products
 *
 * @author Lee Shei Pin
 */
public class ProductManager implements ProductManagerInterface {

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

    public void getProductById(String productId, final ResultCallback<Product> callback) {
        FetchProductByIdTask task = new FetchProductByIdTask(productId) {
            @Override
            protected void onPostExecute(Product result) {
                super.onPostExecute(result);
                if (callback != null)
                    callback.onResult(result);
            }
        };

        task.execute();
    }

    public void updateProduct(String id, String title, String description, String packageType, String imageUrl, String imagePath, final ResultCallback<Product> callback) {
        UpdateProductTask task = new UpdateProductTask() {
            @Override
            protected void onPostExecute(Product result) {
                super.onPostExecute(result);
                if (callback != null)
                    callback.onResult(result);
            }
        };

        task.execute(new String[]{
                id, title, description, packageType, imageUrl, imagePath
        });

    }

    /**
     * AsyncTask for fetching products
     */
    private class FetchProductsTask extends AsyncTask<Integer, Void, List> {
        String companyId;

        public FetchProductsTask(String companyId) {
            this.companyId = companyId;
        }

        @Override
        protected List doInBackground(Integer... integers) {
            int offset = integers[0];
            int limit = integers[1];

            try {
                ParseQuery<ParseObject> productQuery = ParseQuery.getQuery(DbConstants.TABLE_PRODUCT);

                // Get the most recent ones
                productQuery.orderByDescending(DbConstants.CREATED_AT);

                productQuery.include(DbConstants.COMPANY_ID);

                if (companyId != null)
                    productQuery.whereEqualTo(DbConstants.COMPANY_ID, ParseObject.createWithoutData(DbConstants.TABLE_COMPANY, companyId));

                productQuery.setSkip(offset);

                productQuery.setLimit(limit);

                List<Product> productList = new ArrayList<Product>();
                List<ParseObject> result = productQuery.find();

                for (ParseObject productObject : result) {
                    Product product = new Product();
                    product.setId(productObject.getObjectId());
                    product.setTitle(productObject.getString(DbConstants.PRODUCT_TITLE));
                    product.setDescription(productObject.getString(DbConstants.PRODUCT_DESCRIPTION));
                    product.setPackageType(productObject.getString(DbConstants.PRODUCT_PACKAGE_TYPE));
                    product.setCompanyName(productObject.getParseObject(DbConstants.PRODUCT_COMPANY_ID).getString(DbConstants.COMPANY_NAME));
                    product.setImageUrl(productObject.getString(DbConstants.PRODUCT_IMAGE));

                    Log.d(ProductManager.class.getSimpleName(), product.getTitle());

                    productList.add(product);
                }

                return productList;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    /**
     * AsyncTask for fetching product by ID
     */
    private class FetchProductByIdTask extends AsyncTask<Void, Void, Product> {
        final String productId;

        public FetchProductByIdTask(String productId) {
            this.productId = productId;
        }

        @Override
        protected Product doInBackground(Void... voids) {
            try {
                ParseQuery<ParseObject> productQuery = ParseQuery.getQuery(DbConstants.TABLE_PRODUCT);

                productQuery.include(DbConstants.COMPANY_ID);
                ParseObject productObject = productQuery.get(productId);
                Product product = new Product();
                product.setId(productObject.getObjectId());
                product.setTitle(productObject.getString(DbConstants.PRODUCT_TITLE));
                product.setDescription(productObject.getString(DbConstants.PRODUCT_DESCRIPTION));
                product.setPackageType(productObject.getString(DbConstants.PRODUCT_PACKAGE_TYPE));
                product.setCompanyName(productObject.getParseObject(DbConstants.PRODUCT_COMPANY_ID).getString(DbConstants.COMPANY_NAME));
                product.setImageUrl(productObject.getString(DbConstants.PRODUCT_IMAGE));

                return product;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    /**
     * AsyncTask for updating products
     */
    private class UpdateProductTask extends AsyncTask<String, Void, Product> {
        @Override
        protected Product doInBackground(String... params) {
            try {

                String id = params[0];
                String title = params[1];
                String description = params[2];
                String packageType = params[3];
                String imageUrl = params[4];
                String imagePath = params[5];

                ParseQuery<ParseObject> query = ParseQuery.getQuery(DbConstants.TABLE_PRODUCT);
                query.include(DbConstants.COMPANY_ID);
                ParseObject productObject = query.get(id);
                productObject.put(DbConstants.PRODUCT_TITLE, title);
                productObject.put(DbConstants.PRODUCT_DESCRIPTION, description);
                productObject.put(DbConstants.PRODUCT_PACKAGE_TYPE, packageType);

                if (imageUrl != null) {
                    productObject.put(DbConstants.PRODUCT_IMAGE, imageUrl);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                    if (bitmap != null) {
                        // Convert it to byte
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        // Compress image to lower quality scale 1 - 100
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] image = stream.toByteArray();

                        // Create the ParseFile
                        ParseFile file = new ParseFile("product_image.jpg", image);
                        // Upload the image into Parse Cloud
                        file.save();

                        String newImageUrl = file.getUrl();
                        productObject.put(DbConstants.PRODUCT_IMAGE, newImageUrl);

                        File localFile = new File(imagePath);
                        String directory = localFile.getParentFile().getName();
                        if (directory.equals("myfolder") && localFile.exists()) {
                            localFile.delete();
                        }
                    }
                }

                productObject.save();

                Product product = new Product();
                product.setId(productObject.getObjectId());
                product.setTitle(title);
                product.setDescription(description);
                product.setPackageType(packageType);
                product.setImageUrl(productObject.getString(DbConstants.PRODUCT_IMAGE));
                product.setCompanyName(productObject.getParseObject(DbConstants.COMPANY_ID).getString(DbConstants.COMPANY_NAME));

                return product;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * AsyncTask for creating products
     */
    private class CreateProductTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String companyId = params[0];
            String title = params[1];
            String description = params[2];
            String packageType = params[3];
            String imagePath = params[4];

            try {
                ParseObject productObject = new ParseObject(DbConstants.TABLE_PRODUCT);
                productObject.put(DbConstants.COMPANY_ID, ParseObject.createWithoutData(DbConstants.TABLE_COMPANY, companyId));
                productObject.put(DbConstants.PRODUCT_TITLE, title);
                productObject.put(DbConstants.PRODUCT_DESCRIPTION, description);
                productObject.put(DbConstants.PRODUCT_PACKAGE_TYPE, packageType);
                productObject.save();

                String productId = productObject.getObjectId();

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                if (bitmap != null) {
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
                    productObject.put(DbConstants.PRODUCT_IMAGE, imageUrl);

                    File localFile = new File(imagePath);
                    String directory = localFile.getParentFile().getName();
                    if (directory.equals("myfolder") && localFile.exists()) {
                        localFile.delete();
                    }
                }
                productObject.save();

                ParsePush push = new ParsePush();
                push.setMessage("A new product has just been uploaded for review!");
                push.sendInBackground();

            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

}
