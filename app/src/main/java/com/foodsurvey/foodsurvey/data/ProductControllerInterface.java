package com.foodsurvey.foodsurvey.data;

import java.util.List;

public interface ProductControllerInterface {
    public void createProduct(String companyId, String title, String description, String packageType, String imagePath, final ResultCallback<Boolean> callback);

    public void getProducts(int offset, int limit, String companyId, final ResultCallback<List> callback);

    public void updateProduct(String id, String title, String description, String packageType, String imagePath, final ResultCallback<Boolean> callback);
}
