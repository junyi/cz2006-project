package com.foodsurvey.foodsurvey.entity;

/**
 * Represents a user object, can be either administrator or surveyee
 *
 * @author Lee Shei Pin
 */
public class User {
    /**
     * ID of the user
     */
    private String id;

    /**
     * Username of the user (used to login)
     */
    private String username;

    /**
     * First name of the user
     */
    private String firstName;

    /**
     * Last name of the user
     */
    private String lastName;

    /**
     * Age group of the user
     */
    private String ageGroup;

    /**
     * E-mail of the user
     */
    private String email;

    /**
     * Company ID of the user
     * Null if user is surveyee
     * Otherwise indicates the ID of the company of the administrator
     */
    private String companyId;

    /**
     * Company name of the user
     * Null if user is surveyee
     * Otherwise indicates the name of the company of the administrator
     */
    private String companyName;

    public User(String id, String username, String firstName, String lastName, String ageGroup, String email, String companyId, String companyName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ageGroup = ageGroup;
        this.email = email;
        this.companyId = companyId;
        this.companyName = companyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
