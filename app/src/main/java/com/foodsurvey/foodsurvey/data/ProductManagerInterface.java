package com.foodsurvey.foodsurvey.data;

import java.util.List;

/**
 * Interface for the product manager
 * Provides methods to create, obtain and update products
 */
public interface ProductManagerInterface {

    /**
     * Asynchronous method to create new product
     *
     * @param companyId   ID of company of the product
     * @param title       Title of the product
     * @param description Description of the product
     * @param packageType Type of packaging of the product
     * @param imagePath   Path of the image (on the device) of the product
     * @param callback    Callback to receive the result
     */
    public void createProduct(String companyId, String title, String description, String packageType, String imagePath, final ResultCallback<Boolean> callback);

    /**
     * Asynchronous method to retrieve a list of products
     *
     * @param offset    Offset of the first product to fetch
     * @param limit     Maximum number of products to fetch
     * @param companyId Company ID of the product
     * @param callback  Callback to receive the result
     */
    public void getProducts(int offset, int limit, String companyId, final ResultCallback<List> callback);

    /**
     * Asynchronous method to update a product
     *
     * @param id          ID of the product
     * @param title       Title of the product
     * @param description Description of the product
     * @param packageType Type of packaging of the product
     * @param imagePath   Path of the image (on the device) of the product
     * @param callback    Callback to receive the result
     */
    public void updateProduct(String id, String title, String description, String packageType, String imagePath, final ResultCallback<Boolean> callback);
}
