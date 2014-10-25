package com.foodsurvey.foodsurvey;


public interface ResultCallback<T extends Object> {
    public void onResult(T data);
}
