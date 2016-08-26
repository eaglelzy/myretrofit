package com.lizy.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;

/**
 * Created by lizy on 16-8-26.
 */
final class ServiceMethod<T> {

    final okhttp3.Call.Factory callFactory;
    final CallAdapter<?> callAdapter;

    private final HttpUrl baseUrl;
    private final String httpMethod;
    private final String relativeUrl;
    private final Headers headers;
    private final MediaType contentType;
    private final boolean hasBody;
    private final boolean isFormEncoded;
    private final boolean isMultipart;
    private final ParameterHandler<?>[] parameterHandlers;

    ServiceMethod(Builder<T> builder) {
        this.callFactory = builder.retrofit.callFactory();
        this.callAdapter = builder.callAdapter;
        this.baseUrl = builder.retrofit.baseUrl();
        this.httpMethod = builder.httpMethod;
        this.relativeUrl = builder.relativeUrl;
        this.headers = builder.headers;
        this.contentType = builder.contentType;
        this.hasBody = builder.hasBody;
        this.isFormEncoded = builder.isFormEncoded;
        this.isMultipart = builder.isMultipart;
        this.parameterHandlers = builder.parameterHandlers;
    }

    static final class Builder<T> {
        final Retrofit retrofit;
        final Method method;

        Type responseType;
        boolean gotField;
        boolean gotPart;
        boolean gotBody;
        boolean gotPath;
        boolean gotQuery;
        boolean gotUrl;
        String httpMethod;
        boolean hasBody;
        boolean isFormEncoded;
        boolean isMultipart;
        String relativeUrl;
        Headers headers;
        MediaType contentType;
        Set<String> relativeUrlParamNames;
        ParameterHandler<?>[] parameterHandlers;
        CallAdapter<?> callAdapter;

        public Builder(Retrofit retrofit, Method method) {
            this.retrofit = retrofit;
            this.method = method;
        }

        public ServiceMethod build() {
            callAdapter = createCallAdapter();
            return new ServiceMethod<>(this);
        }

        private CallAdapter<?> createCallAdapter() {
            Type returnType = method.getGenericReturnType();
            if (Utils.hasUnresolvableType(returnType)) {
                throw methodError("Method return type must not include a type variable or wildcard: %s", returnType);
            }
            if (returnType == void.class) {
                throw methodError("Service methods cannot return void.");
            }
            Annotation[] annotations = method.getAnnotations();
            try {
                return retrofit.callAdapter(returnType, annotations);
            } catch (RuntimeException e) {
                throw methodError(e, "Unable to create call adapter for %s", returnType);
            }
        }

        private RuntimeException methodError(String message, Object... args) {
            return methodError(null, message, args);
        }

        private RuntimeException methodError(Throwable cause, String message, Object... args) {
            message = String.format(message, args);
            return new IllegalArgumentException(message
                    + "\n    for method "
                    + method.getDeclaringClass().getSimpleName()
                    + "."
                    + method.getName(), cause);
        }
    }
}
