package com.foodsurvey.foodsurvey;

/**
 * Database constants for various
 *
 * @author Hee Jun Yi
 */
public class DbConstants {
    public static final String CREATED_AT = "createdAt";

    public static final String TABLE_COMPANY = "Company";
    public static final String COMPANY_NAME = "name";
    public static final String COMPANY_ID = "companyId";

    public static final String TABLE_PRODUCT = "Product";
    public static final String PRODUCT_TITLE = "title";
    public static final String PRODUCT_DESCRIPTION = "description";
    public static final String PRODUCT_PACKAGE_TYPE = "packageType";
    public static final String PRODUCT_IMAGE = "image";
    public static final String PRODUCT_COMPANY_ID = "companyId";

    public static final String TABLE_REVIEW = "Review";
    public static final String REVIEW_DATA1 = "data1";
    public static final String REVIEW_DATA2 = "data2";
    public static final String REVIEW_DATA3 = "data3";
    public static final String REVIEW_DATA4 = "data4";
    public static final String REVIEW_DATA5 = "data5";
    public static final String REVIEW_IMAGE = "image";
    public static final String REVIEW_PRODUCT_ID = "productId";
    public static final String REVIEW_USER_ID = "userId";

    public static final String TABLE_USER = "_User";
    public static final String USER_ID = "userId";
    public static final String USER_EMAIL = "email";
    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_LAST_NAME = "last_name";
    public static final String USER_AGE_GROUP = "ageGroup";
    public static final String USER_COMPANY_ID = "companyId";
}
