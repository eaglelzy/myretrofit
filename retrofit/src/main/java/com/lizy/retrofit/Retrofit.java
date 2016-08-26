package com.lizy.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by lizy on 16-8-26.
 */
public class Retrofit {

    private final okhttp3.Call.Factory callFactory;
    private final HttpUrl baseUrl;
    private final Map<Method, ServiceMethod> serviceMethodMap = new LinkedHashMap<>();

    private final List<CallAdapter.Factory> adapterFactories;

    Retrofit(okhttp3.Call.Factory callFactory,
             HttpUrl baseUrl,
             List<CallAdapter.Factory> adapterFactories) {
        this.callFactory = callFactory;
        this.baseUrl = baseUrl;
        this.adapterFactories = Collections.unmodifiableList(adapterFactories);
    }

    public <T> T create(final Class<T> server) {
        return (T) Proxy.newProxyInstance(server.getClassLoader(), new Class<?>[]{server},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        ServiceMethod serviceMethod = loadServiceMethod(method);
                        OkHttpCall okHttpCall = new OkHttpCall<>(serviceMethod, args);
                        return serviceMethod.callAdapter.adapt(okHttpCall);
                    }
                });
    }

    ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result;
        synchronized (serviceMethodMap) {
            result = serviceMethodMap.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this, method).build();
                serviceMethodMap.put(method, result);
            }
        }
        return result;
    }

    CallAdapter<?> callAdapter(Type returnType, Annotation[] annotations) {
        return nextCallAdapter(null, returnType, annotations);
    }

    private CallAdapter<?> nextCallAdapter(CallAdapter.Factory skipPast, Type returnType, Annotation[] annotations) {
        Utils.checkNotNull(returnType, "returnType == null");
        Utils.checkNotNull(annotations, "annotations == null");
        int start = adapterFactories.indexOf(skipPast) + 1;
        for (int i = start, count = adapterFactories.size(); i < count; i++) {
            CallAdapter<?> adapter = adapterFactories.get(i).get(returnType, annotations, this);
            if (adapter != null) {
                return adapter;
            }
        }

        StringBuilder builder = new StringBuilder("Could not locate call adapter for ")
                .append(returnType)
                .append(".\n");
        if (skipPast != null) {
            builder.append("  Skipped:");
            for (int i = 0; i < start; i++) {
                builder.append("\n   * ").append(adapterFactories.get(i).getClass().getName());
            }
            builder.append('\n');
        }
        builder.append("  Tried:");
        for (int i = start, count = adapterFactories.size(); i < count; i++) {
            builder.append("\n   * ").append(adapterFactories.get(i).getClass().getName());
        }
        throw new IllegalArgumentException(builder.toString());
    }

    public okhttp3.Call.Factory callFactory() {
        return callFactory;
    }

    public HttpUrl baseUrl() {
        return baseUrl;
    }

    public static final class Builder {
        private okhttp3.Call.Factory callFactory;
        private HttpUrl baseUrl;
        private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();

        public Builder() {

        }

        public Builder client(OkHttpClient client) {
            return callFactory(Utils.checkNotNull(client, "client == null"));
        }

        public Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = Utils.checkNotNull(factory, "factory == null");
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            Utils.checkNotNull(baseUrl, "baseUrl == null");
            HttpUrl httpUrl = HttpUrl.parse(baseUrl);
            if (httpUrl == null) {
                throw new IllegalArgumentException("Illegal URL: " + baseUrl);
            }
            return baseUrl(httpUrl);
        }

        public Builder baseUrl(HttpUrl baseUrl) {
            Utils.checkNotNull(baseUrl, "baseUrl == null");
            List<String> pathSegments = baseUrl.pathSegments();
            if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
                throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
            }

            this.baseUrl = baseUrl;
            return this;
        }

        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            adapterFactories.add(Utils.checkNotNull(factory, "factory == null"));
            return this;
        }

        public Retrofit build() {
            if (baseUrl == null) {
                throw new IllegalArgumentException("Base URL required.");
            }

            okhttp3.Call.Factory callFactory = this.callFactory;
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }

            List<CallAdapter.Factory> adapterFactories = new ArrayList<>(this.adapterFactories);
            adapterFactories.add(new DefaultCallAdapterFactory());

            return new Retrofit(callFactory, baseUrl, adapterFactories);
        }
    }
}
