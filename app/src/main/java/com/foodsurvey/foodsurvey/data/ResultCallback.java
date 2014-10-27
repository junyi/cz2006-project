package com.foodsurvey.foodsurvey.data;

/**
 * Generic callback method for receiving results
 * @param <T> Type of data to be received
 */
public interface ResultCallback<T extends Object> {
    public void onResult(T data);
}
