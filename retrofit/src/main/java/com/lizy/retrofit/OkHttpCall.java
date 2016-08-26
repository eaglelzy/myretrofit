package com.lizy.retrofit;

import java.io.IOException;

import okhttp3.Request;

/**
 * Created by lizy on 16-8-26.
 */
public class OkHttpCall<T> implements Call<T> {
    private final ServiceMethod<T> serviceMethod;
    private final Object[] args;

    public OkHttpCall(ServiceMethod<T> serviceMethod, Object[] args) {
        this.serviceMethod = serviceMethod;
        this.args = args;
    }

    @Override
    public Response<T> execute() throws IOException {
        return null;
    }

    @Override
    public void enqueue(Callback<T> callback) {
        callback.onResponse(this, new Response<T>());
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Call<T> clone() {
        return null;
    }

    @Override
    public Request request() {
        return null;
    }
}
