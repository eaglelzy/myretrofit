package com.lizy.retrofit;

/**
 * Created by lizy on 16-8-26.
 */
public interface Callback<T> {

    void onResponse(Call<T> call, Response<T> response);

    void onFailure(Call<T> call, Throwable t);
}
