package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.ICookie;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author :zhanjixun
 * @date : 2018/10/9 16:30
 */
public class HttpClientExecutor extends BaseExecutor {

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    @Override
    public Response execute(Request request) {


        return null;
    }

    @Override
    public void addCookie(ICookie cookie) {

    }

    @Override
    public ICookie[] getCookies() {
        return new ICookie[0];
    }

    @Override
    public void clearCookies() {

    }
}
