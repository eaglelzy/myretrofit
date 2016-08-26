package com.lizy.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by lizy on 16-8-26.
 */
public class DefaultCallAdapterFactory extends CallAdapter.Factory {
    static final CallAdapter.Factory INSTANCE = new DefaultCallAdapterFactory();
    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != Call.class) {
            return null;
        }

        final Type responseType = Utils.getCallResponseType(returnType);
        return new CallAdapter<Call<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public <R> Call<?> adapt(Call<R> call) {
                return call;
            }
        };
    }
}
