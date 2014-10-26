package com.foodsurvey.foodsurvey.data;

public class Controllers {
    private static ReviewControllerInterface reviewController = null;
    private static ProductControllerInterface productController = null;
    private static UserControllerInterface userController = null;

    public static ReviewControllerInterface getReviewController() {
        if (reviewController == null)
            reviewController = new ReviewController();
        return reviewController;
    }

    public static ProductControllerInterface getProductController() {
        if (productController == null)
            productController = new ProductController();
        return productController;
    }

    public static UserControllerInterface getUserController() {
        if (userController == null)
            userController = new UserController();
        return userController;
    }
}
