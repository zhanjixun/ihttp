package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.parsing.Configuration;
import com.zhanjixun.ihttp.parsing.HttpProxy;
import com.zhanjixun.ihttp.utils.StrUtils;
import com.zhanjixun.ihttp.utils.Util;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author :zhanjixun
 * @date : 2018/11/28 23:35
 */
@Slf4j
public class OkHttpExecutor extends BaseExecutor {

    private final OkHttpClient okHttpClient;

    public OkHttpExecutor(Configuration configuration, CookiesStore cookiesStore) {
        super(configuration, cookiesStore);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new MyCookieJar(cookiesStore));
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        if (configuration.getProxy() != null) {
            HttpProxy proxy = configuration.getProxy();
            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHostName(), configuration.getProxy().getPort())));
            builder.hostnameVerifier((s, sslSession) -> proxy.isTrustSSL());
        }
        okHttpClient = builder.build();
    }


    @Override
    protected Response doPostMethod(Request request) throws IOException {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.url(request.getUrl());
        request.getHeaders().forEach(h -> builder.addHeader(h.getName(), h.getValue()));

        String charset = Optional.ofNullable(request.getCharset()).orElse("utf-8");
        //参数
        if (Util.isNotEmpty(request.getParams())) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            request.getParams().forEach(p -> bodyBuilder.add(p.getName(), p.getValue()));
            builder.post(bodyBuilder.build());
        }
        //请求体
        if (Util.isNotBlank(request.getBody())) {
            builder.post(FormBody.create(MediaType.parse(String.format("application/json;charset=%s", charset)), request.getBody()));
        }
        //文件
        if (Util.isNotEmpty(request.getFileParts())) {


        }
        return executeMethod(request, builder.build());
    }

    @Override
    protected Response doDeleteMethod(Request request) throws IOException {
        return null;
    }

    @Override
    protected Response doPutMethod(Request request) throws IOException {
        return null;
    }

    @Override
    protected Response doPatchMethod(Request request) throws IOException {
        return null;
    }

    @Override
    protected Response doTraceMethod(Request request) throws IOException {
        return null;
    }

    @Override
    protected Response doOptionsMethod(Request request) throws IOException {
        return null;
    }

    @Override
    protected Response doHeadMethod(Request request) throws IOException {
        return null;
    }

    @Override
    protected Response doGetMethod(Request request) throws IOException {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.url(StrUtils.addQuery(request.getUrl(), request.getParams()));
        request.getHeaders().forEach(h -> builder.addHeader(h.getName(), h.getValue()));

        return executeMethod(request, builder.build());
    }

    private Response executeMethod(Request request, okhttp3.Request okRequest) throws IOException {
        okhttp3.Response execute = okHttpClient.newCall(okRequest).execute();

        Response response = new Response();
        response.setRequest(request);
        response.setStatus(execute.code());
        response.setBody(execute.body().bytes());
        response.setHeaders(execute.headers().toMultimap());
        return response;
    }

    //https://github.com/franmontiel/PersistentCookieJar
    static class MyCookieJar implements CookieJar {

        private final CookiesStore cookiesStore;

        public MyCookieJar(CookiesStore cookiesStore) {
            this.cookiesStore = cookiesStore;
        }

        @Override
        public void saveFromResponse(HttpUrl url, List<okhttp3.Cookie> cookies) {
            cookiesStore.addCookies(cookies.stream().map(originCookie -> {
                com.zhanjixun.ihttp.cookie.Cookie iCookie = new com.zhanjixun.ihttp.cookie.Cookie();
                iCookie.setDomain(originCookie.domain());
                iCookie.setPath(originCookie.path());
                iCookie.setName(originCookie.name());
                iCookie.setValue(originCookie.value());
                iCookie.setExpiryDate(new Date(originCookie.expiresAt()));
                iCookie.setSecure(originCookie.secure());
                iCookie.setHttpOnly(originCookie.httpOnly());
                return iCookie;
            }).collect(Collectors.toList()));
        }

        @Override
        public List<okhttp3.Cookie> loadForRequest(HttpUrl url) {
            //清除过期cookie
            cookiesStore.clearExpired();
            //转换cookie类
            List<Cookie> cookieList = cookiesStore.getCookies().stream().map(originCookie -> {
                okhttp3.Cookie.Builder build = new okhttp3.Cookie.Builder();
                build.domain(originCookie.getDomain());
                build.path(originCookie.getPath());
                build.name(originCookie.getName());
                build.value(originCookie.getValue());
                build.expiresAt(originCookie.getExpiryDate().getTime());
                if (originCookie.isSecure()) {
                    build.secure();
                }
                if (originCookie.isHttpOnly()) {
                    build.httpOnly();
                }
                return build.build();
            }).collect(Collectors.toList());
            //过滤出符合url
            return cookieList.stream().filter(d -> d.matches(url)).collect(Collectors.toList());
        }
    }


}
