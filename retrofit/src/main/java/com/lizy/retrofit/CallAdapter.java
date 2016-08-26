package com.lizy.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by lizy on 16-8-26.
 */
public interface CallAdapter<T> {

    Type responseType();

    <R> T adapt(Call<R> call);

    abstract class Factory {
        public abstract CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit);

        protected static Type getParameterUpperBound(int index, ParameterizedType type) {
            return Utils.getParameterUpperBound(index, type);
        }

        protected static Class<?> getRawType(Type type) {
            return Utils.getRawType(type);
        }
    }
}
