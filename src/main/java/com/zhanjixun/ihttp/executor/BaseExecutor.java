package com.zhanjixun.ihttp.executor;

import com.alibaba.fastjson.JSON;
import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.POST;
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
        Response response = null;
        if (request.getMethod().equals(GET.class.getSimpleName())) {
            response = doGetMethod(request);
        }
        if (request.getMethod().equals(POST.class.getSimpleName())) {
            response = doPostMethod(request);
        }
        if (response == null) {
            throw new RuntimeException("未能识别的HTTP请求方法：" + request.getMethod());
        }
        return response;
    }

    protected abstract Response doGetMethod(Request request);

    protected abstract Response doPostMethod(Request request);

    @Override
    public void cacheCookie(File cacheFile) {
        if (CollectionUtils.isNotEmpty(getCookies())) {
            try {
                Okio.buffer(Okio.sink(cacheFile)).writeUtf8(JSON.toJSONString(getCookies())).flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int loadCookieCache(File cacheFile) {
        try {
            String json = Okio.buffer(Okio.source(cacheFile)).readUtf8();
            List<Cookie> cookie = JSON.parseArray(json, Cookie.class);
            addCookies(cookie);
            return cookie.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
