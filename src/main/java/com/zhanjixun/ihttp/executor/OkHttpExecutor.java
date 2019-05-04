package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.domain.HttpProxy;
import com.zhanjixun.ihttp.domain.NameValuePair;
import com.zhanjixun.ihttp.utils.CookieUtils;
import com.zhanjixun.ihttp.utils.StrUtils;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author :zhanjixun
 * @date : 2018/11/28 23:35
 */
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
        for (String n : execute.headers().names()) {
            for (String v : execute.headers(n)) {
                response.getHeaders().add(new NameValuePair(n, v));
            }
        }
        return response;
    }

    //https://github.com/franmontiel/PersistentCookieJar
    class MyCookieJar implements CookieJar {

        private CookiesStore cookiesStore;

        public MyCookieJar(CookiesStore cookiesStore) {
            this.cookiesStore = cookiesStore;
        }

        @Override
        public void saveFromResponse(HttpUrl url, List<okhttp3.Cookie> cookies) {
            cookiesStore.addCookies(cookies.stream().map(CookieUtils::okhttpConvert).collect(Collectors.toList()));
        }

        @Override
        public List<okhttp3.Cookie> loadForRequest(HttpUrl url) {
            //清除过期cookie
            cookiesStore.clearExpired();
            //转换cookie类
            List<Cookie> cookieList = cookiesStore.getCookies().stream().map(CookieUtils::okhttpConvert).collect(Collectors.toList());
            //过滤出符合url
            return cookieList.stream().filter(d -> d.matches(url)).collect(Collectors.toList());
        }
    }


}
