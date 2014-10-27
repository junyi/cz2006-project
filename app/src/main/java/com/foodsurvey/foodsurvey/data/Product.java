package com.foodsurvey.foodsurvey.data;

import org.parceler.Parcel;

/**
 * Represents a product uploaded by a company
 */
@Parcel
public class Product {
    /**
     * ID of the product
     */
    String id;

    /**
     * Title of the product
     */
    String title;

    /**
     * Description of the product
     */
    String description;

    /**
     * Company name of the product
     */
    String companyName;

    /**
     * Packaging type of the product
     */
    String packageType;

    /**
     * Image URL of the product's image
     */
    String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }
}
