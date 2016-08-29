package com.lizy.retrofit;

import com.lizy.retrofit.helper.ToStringConverterFactory;
import com.lizy.retrofit.http.GET;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by lizy on 16-8-29.
 */
public class CallTest {
    @Rule public final MockWebServer server = new MockWebServer();

    interface Service {
        @GET("/") Call<String> getString();
    }

    @Test public void http200Sync() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(new ToStringConverterFactory())
                .build();

        Service example = retrofit.create(Service.class);

        server.enqueue(new MockResponse().setBody("hi"));

        Response<String> response = example.getString().execute();
        assertThat(response.isSuccessed()).isTrue();
        assertThat(response.body()).isEqualTo("hi");
    }

    @Test public void http404Sync() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(new ToStringConverterFactory())
                .build();

        Service example = retrofit.create(Service.class);

        server.enqueue(new MockResponse().setResponseCode(404).setBody("error"));

        Response<String> response = example.getString().execute();
        assertThat(response.isSuccessed()).isFalse();
        assertThat(response.code()).isEqualTo(404);
        assertThat(response.body()).isNull();
        assertThat(response.errorBody().string()).isEqualTo("error");
    }

    @Test public void transportProblemSync() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Service example = retrofit.create(Service.class);

        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        Call<String> call = example.getString();
        try {
            call.execute();
            fail();
        } catch (IOException ignored) {
            System.out.println(ignored);
        }
    }

}
