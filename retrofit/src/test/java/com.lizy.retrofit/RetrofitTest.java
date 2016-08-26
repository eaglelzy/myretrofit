package com.lizy.retrofit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizy on 16-8-26.
 */
public class RetrofitTest {

    @Test
    public void test() throws Exception {
        List<CallAdapter.Factory> factories = new ArrayList<>();
        factories.add(new DefaultCallAdapterFactory());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.github.com")
                .addCallAdapterFactory(new DefaultCallAdapterFactory())
                .build();
        IServer server = retrofit.create(IServer.class);
        Call<String> call = server.loadOrderList("10001");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("onresponse: " + response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
