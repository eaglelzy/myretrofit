package com.lizy.retrofit.converters.gson;

import com.lizy.retrofit.Call;
import com.lizy.retrofit.Response;
import com.lizy.retrofit.Retrofit;
import com.lizy.retrofit.http.Body;
import com.lizy.retrofit.http.POST;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by lizy on 16-8-29.
 */
public class GsonResponseBodyConverterTest {

    static class User {
        public String name;
        public String password;

        @Override
        public String toString() {
            return name;
        }
    }

    static class LoginResponse {
        public String message;
    }

    interface Service {
        @POST
        Call<LoginResponse> login(@Body User user);
    }

    private Service service;

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Before public void setUp() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(Service.class);
    }

    @Test
    public void login() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"message\":\"login successed\"}"));

        User requestBody = new User();
        requestBody.name = "name";
        requestBody.password = "password";
        Call<LoginResponse> call = service.login(requestBody);
        Response<LoginResponse> response = call.execute();
        LoginResponse res = response.body();
        assertThat(res.message).isEqualTo("login successed");

        RecordedRequest request = server.takeRequest();
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"name\":\"name\",\"password\":\"password\"}");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }

    @After
    public void end() throws IOException {
        server.shutdown();
    }
}