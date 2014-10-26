package com.foodsurvey.foodsurvey.data;


public interface ResultCallback<T extends Object> {
    public void onResult(T data);
}
