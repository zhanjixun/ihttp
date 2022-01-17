package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.parsing.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class BaseExecutor implements Executor {

    protected final Configuration configuration;
    @Getter
    protected final CookiesStore cookiesStore;

    public BaseExecutor(Configuration configuration, CookiesStore cookiesStore) {
        this.configuration = configuration;
        this.cookiesStore = cookiesStore;
    }

    @Override
    public final Response execute(Request request) throws IOException {
        long startTime = System.currentTimeMillis();
        Response response = executeRequest(request);
        long endTime = System.currentTimeMillis();
        log.debug(String.format("%s %d [%dms] %s", request.getMethod(), response.getStatus(), (endTime - startTime), request.getUrl()));
        return response;
    }

    /**
     * 执行请求
     *
     * @param request
     * @return
     */
    protected abstract Response executeRequest(Request request) throws IOException;

}
