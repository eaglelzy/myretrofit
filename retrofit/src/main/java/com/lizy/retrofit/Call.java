package com.lizy.retrofit;

import java.io.IOException;

import okhttp3.Request;

/**
 * Created by lizy on 16-8-26.
 */
public interface Call<T> extends Cloneable {

    Response<T> execute() throws IOException;

    void enqueue(Callback<T> callback);

    boolean isExecuted();

    void cancel();

    boolean isCanceled();

    Call<T> clone();

    Request request();
}
