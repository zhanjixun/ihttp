package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.POST;
import com.zhanjixun.ihttp.domain.Cookie;
import lombok.extern.log4j.Log4j;
import okio.Okio;

import java.io.IOException;
import java.net.*;
import java.util.List;


/**
 * Java原生http接口
 *
 * @author :zhanjixun
 * @date : 2018/10/9 16:02
 */
@Log4j
public class JavaExecutor extends BaseExecutor {

    public JavaExecutor() {
        CookieHandler.setDefault(new CookieManager());
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

    private Response doPostMethod(Request request) {
        return null;
    }

    private Response doGetMethod(Request request) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(request.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(request.isFollowRedirects());
            //发送请求
            connection.connect();

            Response response = new Response();
            response.setStatus(connection.getResponseCode());
            response.setBody(Okio.buffer(Okio.source(connection.getInputStream())).readByteArray());

        } catch (MalformedURLException e) {
            throw new RuntimeException("构建URL失败" + request.getUrl(), e);
        } catch (IOException e) {
            throw new RuntimeException("HTTP请求失败", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }


    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public List<Cookie> getCookies() {
        return null;
    }

    @Override
    public void clearCookies() {

    }

    @Override
    public void addCookies(List<Cookie> cookie) {
    }
}
