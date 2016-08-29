package com.lizy.retrofit;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by lizy on 16-8-26.
 */
public class RequestBuilder {
    private final Request.Builder requestBuilder;
    private final HttpUrl baseUrl;
    private final String method;
    private final String relativeUrl;
    private final MediaType contentType;
    private final boolean hasBody;

    private RequestBody body;
    private HttpUrl.Builder urlBuilder;
    private FormBody.Builder formBuilder;
    private MultipartBody.Builder multipartBuilder;

    public RequestBuilder(String httpMethod,
                          HttpUrl baseUrl,
                          String relativeUrl,
                          Headers headers,
                          MediaType contentType,
                          boolean hasBody,
                          boolean isFormEncoded,
                          boolean isMultipart) {
        this.method = httpMethod;
        this.baseUrl = baseUrl;
        this.requestBuilder = new Request.Builder();
        this.relativeUrl = relativeUrl;
        if (headers != null) {
            requestBuilder.headers(headers);
        }
        this.contentType = contentType;
        this.hasBody = hasBody;

        if (isFormEncoded) {
            formBuilder = new FormBody.Builder();
        } else if (isMultipart) {
            multipartBuilder = new MultipartBody.Builder();
            multipartBuilder.setType(MultipartBody.FORM);
        }
    }

    public Request build() {
        HttpUrl url;
        HttpUrl.Builder urlBuilder = this.urlBuilder;
        if (urlBuilder != null) {
            url = urlBuilder.build();
        } else {
            url = baseUrl;
//            url = baseUrl.resolve(relativeUrl);
//            if (url == null) {
//                throw new IllegalArgumentException("Malformed URL. Base " + baseUrl +
//                        "relative:" + relativeUrl);
//            }
        }

        if (body == null) {
            if (formBuilder != null) {
                body = formBuilder.build();
            } else if (multipartBuilder != null) {
                body = multipartBuilder.build();
            } else if (hasBody) {
                body = RequestBody.create(null, new byte[0]);
            }
        }

        MediaType contentType = this.contentType;
        if (contentType != null) {
            if (body != null) {
                body = new ContentTypeOverridingRequestBody(body, contentType);
            } else {
                requestBuilder.addHeader("Content-Type", contentType.toString());
            }
        }
        return requestBuilder
                .url(url)
                .method(method, body)
                .build();
    }

    public void setBody(RequestBody body) {
        this.body = body;
    }

    private static class ContentTypeOverridingRequestBody extends RequestBody {
        private final RequestBody delegate;
        private final MediaType contentType;

        ContentTypeOverridingRequestBody(RequestBody delegate, MediaType contentType) {
            this.delegate = delegate;
            this.contentType = contentType;
        }

        @Override public MediaType contentType() {
            return contentType;
        }

        @Override public long contentLength() throws IOException {
            return delegate.contentLength();
        }

        @Override public void writeTo(BufferedSink sink) throws IOException {
            delegate.writeTo(sink);
        }
    }
}
