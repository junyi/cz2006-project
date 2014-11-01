package com.foodsurvey.foodsurvey.control;

/**
 * Top level manager class to obtain individual managers
 * Uses singleton pattern to instantiate the necessary managers
 *
 * @author Hee Jun Yi
 */
public class Managers {
    private static ReviewManagerInterface reviewManager = null;
    private static ProductManagerInterface productManager = null;
    private static UserManagerInterface userManager = null;

    /**
     * Method to obtain the manager for reviews
     *
     * @return reviewManager The review manager
     */
    public static ReviewManagerInterface getReviewManager() {
        if (reviewManager == null)
            reviewManager = new ReviewManager();
        return reviewManager;
    }

    /**
     * Method to obtain the manager for products
     *
     * @return productManager The product manager
     */
    public static ProductManagerInterface getProductManager() {
        if (productManager == null)
            productManager = new ProductManager();
        return productManager;
    }

    /**
     * Method to obtain the manager for users
     *
     * @return userManager The user manager
     */
    public static UserManagerInterface getUserManager() {
        if (userManager == null)
            userManager = new UserManager();
        return userManager;
    }
}
