package com.lizy.retrofit;

import com.lizy.retrofit.http.Get;

/**
 * Created by lizy on 16-8-26.
 */
public interface IServer {
    @Get("http://www.github.com")
    Call<String> loadOrderList(String id);
}
