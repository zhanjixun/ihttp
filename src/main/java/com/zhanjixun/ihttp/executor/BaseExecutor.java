package com.zhanjixun.ihttp.executor;

import com.alibaba.fastjson.JSON;
import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.domain.Cookie;
import lombok.extern.log4j.Log4j;
import okio.Okio;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Log4j
public abstract class BaseExecutor implements CookiesStore {

    protected final Configuration configuration;

    public BaseExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    public Response execute(Request request) {
        Response response;
        switch (request.getMethod()) {
            case "GET":
                response = doGetMethod(request);
                break;
            case "POST":
                response = doPostMethod(request);
                break;
            default:
                throw new RuntimeException("未能识别的HTTP请求方法：" + request.getMethod());
        }
        //do something before http execute
        return response;
    }

    protected abstract Response doGetMethod(Request request);

    protected abstract Response doPostMethod(Request request);

    @Override
    public void cacheCookie(File cacheFile) {
        if (CollectionUtils.isNotEmpty(getCookies())) {
            try {
                String string = JSON.toJSONString(getCookies());
                log.info("cache cookie " + string);
                Okio.buffer(Okio.sink(cacheFile)).writeUtf8(string).flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int loadCookieCache(File cacheFile) {
        try {
            String json = Okio.buffer(Okio.source(cacheFile)).readUtf8();
            log.info("load cookie cache " + json);
            List<Cookie> cookie = JSON.parseArray(json, Cookie.class);
            addCookies(cookie);
            return cookie.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
