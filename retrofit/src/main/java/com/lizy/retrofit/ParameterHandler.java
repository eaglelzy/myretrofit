package com.lizy.retrofit;

import java.io.IOException;

import okhttp3.RequestBody;

/**
 * Created by lizy on 16-8-26.
 */
abstract class ParameterHandler<T> {
    abstract void apply(RequestBuilder builder, T value) throws IOException;

    static final class Body<T> extends ParameterHandler<T> {
        private final Converter<T, RequestBody> converter;

        public Body(Converter<T, RequestBody> converter) {
            this.converter = converter;
        }

        @Override
        void apply(RequestBuilder builder, T value) {
            RequestBody body;
            try {
                body = converter.convert(value);
            } catch (IOException e) {
                throw new RuntimeException("Unable to convert " + value + " to RequestBody", e);
            }
            builder.setBody(body);
        }
    }
}
