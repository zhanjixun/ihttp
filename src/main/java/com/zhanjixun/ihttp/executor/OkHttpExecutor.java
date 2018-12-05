package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.domain.Cookie;
import com.zhanjixun.ihttp.domain.HttpProxy;
import com.zhanjixun.ihttp.domain.NameValuePair;
import com.zhanjixun.ihttp.utils.StrUtils;
import lombok.Getter;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author :zhanjixun
 * @date : 2018/11/28 23:35
 */
public class OkHttpExecutor extends BaseExecutor {

    private final MyCookieJar myCookieJar = new MyCookieJar();
    private final OkHttpClient okHttpClient;

    public OkHttpExecutor(Configuration configuration) {
        super(configuration);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(myCookieJar);
        if (configuration.getProxy() != null) {
            HttpProxy proxy = configuration.getProxy();
            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHostName(), configuration.getProxy().getPort())));
        }
        okHttpClient = builder.build();
    }


    @Override
    protected Response doPostMethod(Request request) {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.url(request.getUrl());
        request.getHeaders().forEach(h -> builder.addHeader(h.getName(), h.getValue()));

        String charset = Optional.ofNullable(request.getCharset()).orElse("utf-8");
        //参数
        if (CollectionUtils.isNotEmpty(request.getParams())) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            request.getParams().forEach(p -> bodyBuilder.add(p.getName(), p.getValue()));
            builder.post(bodyBuilder.build());
        }
        //请求体
        if (StringUtils.isNotBlank(request.getBody())) {
            builder.post(FormBody.create(MediaType.parse(String.format("application/json;charset=%s", charset)), request.getBody()));
        }
        //文件
        if (CollectionUtils.isNotEmpty(request.getFileParts())) {


        }
        return executeMethod(request, builder.build());
    }

    @Override
    protected Response doGetMethod(Request request) {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.url(StrUtils.addQuery(request.getUrl(), request.getParams()));
        request.getHeaders().forEach(h -> builder.addHeader(h.getName(), h.getValue()));

        return executeMethod(request, builder.build());
    }

    private Response executeMethod(Request request, okhttp3.Request okRequest) {
        try {
            okhttp3.Response execute = okHttpClient.newCall(okRequest).execute();

            Response response = new Response();
            response.setRequest(request);
            response.setStatus(execute.code());
            response.setBody(execute.body().bytes());
            for (String n : execute.headers().names()) {
                for (String v : execute.headers(n)) {
                    response.getHeaders().add(new NameValuePair(n, v));
                }
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addCookies(List<Cookie> cookies) {
        myCookieJar.getCookieList().addAll(cookies.stream().map(c -> {
            okhttp3.Cookie.Builder build = new okhttp3.Cookie.Builder();
            build.domain(c.getDomain());
            build.path(c.getPath());
            build.name(c.getName());
            build.value(c.getValue());
            build.expiresAt(c.getExpiryDate().getTime());
            if (c.isSecure()) {
                build.secure();
            }
            if (c.isHttpOnly()) {
                build.httpOnly();
            }
            return build.build();

        }).collect(Collectors.toList()));
    }

    @Override
    public void addCookie(Cookie cookie) {
        okhttp3.Cookie.Builder build = new okhttp3.Cookie.Builder();
        build.domain(cookie.getDomain());
        build.path(cookie.getPath());
        build.name(cookie.getName());
        build.value(cookie.getValue());
        build.expiresAt(cookie.getExpiryDate().getTime());
        if (cookie.isSecure()) {
            build.secure();
        }
        if (cookie.isHttpOnly()) {
            build.httpOnly();
        }
        myCookieJar.getCookieList().add(build.build());
    }

    @Override
    public List<Cookie> getCookies() {
        return null;
    }

    @Override
    public void clearCookies() {
        myCookieJar.getCookieList().clear();
    }

    class MyCookieJar implements CookieJar {
        @Getter
        private final List<okhttp3.Cookie> cookieList = new ArrayList<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<okhttp3.Cookie> cookies) {
            cookieList.addAll(cookies);
        }

        @Override
        public List<okhttp3.Cookie> loadForRequest(HttpUrl url) {
            List<okhttp3.Cookie> collect = cookieList.stream().filter(c -> url.host().endsWith(c.domain()))
                    .filter(c -> c.expiresAt() < System.currentTimeMillis())
                    .filter(c -> !c.httpOnly() || url.scheme().equals("http") || url.scheme().equals("https"))
                    .collect(Collectors.toList());
            return collect;
        }
    }

}
