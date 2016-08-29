package com.lizy.retrofit;

import com.lizy.retrofit.http.GET;
import com.lizy.retrofit.http.Path;

/**
 * Created by lizy on 16-8-26.
 */
public interface IServer {
    @GET("http://www.github.com")
    Call<String> loadOrderList(@Path("id") String id);
}
