package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.POST;
import com.zhanjixun.ihttp.domain.Cookie;
import com.zhanjixun.ihttp.domain.NameValuePair;
import lombok.extern.log4j.Log4j;
import okio.Okio;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author :zhanjixun
 * @date : 2018/10/9 16:30
 */
@Log4j
public class ComponentsHttpClientExecutor extends BaseExecutor {

    private final BasicCookieStore cookieStore;
    private final HttpClient httpClient;

    public ComponentsHttpClientExecutor() {
        cookieStore = new BasicCookieStore();
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).setConnectionTimeToLive(5, TimeUnit.SECONDS).build();
    }

    @Override
    public Response execute(Request request) {
        if (request.getMethod().equals(GET.class.getSimpleName())) {
            return doGetMethod(request);
        }
        if (request.getMethod().equals(POST.class.getSimpleName())) {
            return doPostMethod(request);
        }
        throw new RuntimeException("未能识别的http请求方法：" + request.getMethod());
    }

    private Response doGetMethod(Request request) {
        String paramQuery = request.getParams().stream().map(d -> d.getName() + "=" + d.getValue()).collect(Collectors.joining("&"));
        if (StringUtils.isNotBlank(paramQuery)) {
            request.setUrl(request.getUrl().contains("?") ? request.getUrl() + "&" + paramQuery : request.getUrl() + "?" + paramQuery);
        }

        HttpGet method = new HttpGet(request.getUrl());
        method.setConfig(RequestConfig.custom().setRedirectsEnabled(request.isFollowRedirects()).build());

        for (NameValuePair nameValuePair : request.getHeaders()) {
            method.addHeader(nameValuePair.getName(), nameValuePair.getValue());
        }

        return executeMethod(method, request);
    }

    private Response doPostMethod(Request request) {
        HttpPost method = new HttpPost(request.getUrl());
        for (NameValuePair nameValuePair : request.getHeaders()) {
            method.addHeader(nameValuePair.getName(), nameValuePair.getValue());
        }

        //直接请求体
        if (StringUtils.isNotBlank(request.getBody())) {
            try {
                String contentType = Optional.ofNullable(method.getFirstHeader("Content-Type")).map(org.apache.http.NameValuePair::getValue).orElse("text/html");

                StringEntity stringEntity = new StringEntity(request.getBody());
                stringEntity.setContentType(contentType);
                method.setEntity(stringEntity);
            } catch (UnsupportedEncodingException e) {
                log.error("构造请求体StringBody出错", e);
            }
        }
        return executeMethod(method, request);
    }

    private Response executeMethod(HttpUriRequest method, Request request) {
        try {
            HttpResponse httpResponse = httpClient.execute(method);

            String charset = request.getResponseCharset();
            HeaderElement[] elements = httpResponse.getEntity().getContentType().getElements();
            if (elements.length == 1) {
                org.apache.http.NameValuePair param = elements[0].getParameterByName("charset");
                if (param != null) {
                    charset = param.getValue();
                }
            }
            if (charset == null) {
                charset = request.getCharset();
            }

            Response response = new Response();
            response.setRequest(request);
            response.setStatus(httpResponse.getStatusLine().getStatusCode());
            response.setBody(Okio.buffer(Okio.source(httpResponse.getEntity().getContent())).readByteArray());
            response.setCharset("utf-8");
            return response;
        } catch (IOException e) {
            log.error("发送" + request.getMethod() + "请求失败", e);
        }
        return null;
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookieStore.addCookie(copyProperties(cookie, new BasicClientCookie(cookie.getName(), cookie.getValue())));
    }

    @Override
    public List<Cookie> getCookies() {
        return cookieStore.getCookies().stream()
                .map(c -> copyProperties(c, new Cookie()))
                .collect(Collectors.toList());
    }

    @Override
    public void clearCookies() {
        cookieStore.clear();
    }

    @Override
    public void addCookies(List<Cookie> cookie) {
        cookieStore.addCookies(cookie.stream().map(c -> copyProperties(c, new BasicClientCookie(c.getName(), c.getValue()))).toArray(BasicClientCookie[]::new));
    }
}
