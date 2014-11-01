package com.foodsurvey.foodsurvey.entity;

/**
 * Represents a review of a product by a surveyee
 *
 * @author Hee Jun Yi
 */
public class Review {
    /**
     * ID of the review in the database
     */
    private String id;

    /**
     * Data 1 of the review
     */
    private String data1;

    /**
     * Data 2 of the review
     */
    private String data2;

    /**
     * Data 3 of the review
     */
    private String data3;

    /**
     * Data 4 of the review
     */
    private String data4;

    /**
     * Data 5 of the review
     */
    private String data5;

    /**
     * Image URL of the review
     */
    private String imageUrl;

    /**
     * Age group of surveyee who did the review
     */
    private String ageGroup;

    /**
     * Product ID of the associated product for the review
     */
    private String productId;

    /**
     * User ID of the surveyee
     */
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public String getData5() {
        return data5;
    }

    public void setData5(String data5) {
        this.data5 = data5;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
