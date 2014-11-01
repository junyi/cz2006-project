package com.foodsurvey.foodsurvey.control;

/**
 * Generic callback method for receiving results
 *
 * @param <T> Type of data to be received
 * @author Hee Jun Yi
 */
public interface ResultCallback<T extends Object> {
    public void onResult(T data);
}
