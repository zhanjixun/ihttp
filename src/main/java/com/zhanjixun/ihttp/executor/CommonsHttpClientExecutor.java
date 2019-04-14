package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import com.zhanjixun.ihttp.logging.ConnectionInfo;
import com.zhanjixun.ihttp.utils.StrUtils;
import lombok.extern.log4j.Log4j;
import okio.Okio;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基于org.apache.commons.httpclient.HttpClient的http框架
 *
 * @author zhanjixun
 * @see HttpClient
 */
@Log4j
public class CommonsHttpClientExecutor extends BaseExecutor {

    private final HttpClient httpClient = new HttpClient();

    public CommonsHttpClientExecutor(Configuration configuration) {
        super(configuration);

        //设置代理服务器
        if (configuration.getProxy() != null) {
            httpClient.getHostConfiguration().setProxy(configuration.getProxy().getHostName(), configuration.getProxy().getPort());
        }

    }

    @Override
    protected Response doGetMethod(Request request) {
        GetMethod method = new GetMethod(StrUtils.addQuery(request.getUrl(), request.getParams()));
        method.setFollowRedirects(request.isFollowRedirects());
        request.getHeaders().forEach(h -> method.addRequestHeader(h.getName(), h.getValue()));
        return executeMethod(method, request);
    }

    @Override
    protected Response doPostMethod(Request request) {
        PostMethod method = new PostMethod(request.getUrl());
        request.getHeaders().forEach(h -> method.addRequestHeader(h.getName(), h.getValue()));
        request.getParams().forEach(p -> method.addParameter(p.getName(), p.getValue()));

        //直接请求体
        if (StringUtils.isNotBlank(request.getBody())) {
            String contentType = Optional.ofNullable(method.getRequestHeader("Content-Type"))
                    .orElse(new Header("", "application/json")).getValue();
            try {
                method.setRequestEntity(new StringRequestEntity(request.getBody(), contentType, request.getCharset()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        //文件上传
        if (CollectionUtils.isNotEmpty(request.getFileParts())) {
            Part[] parts = new Part[request.getFileParts().size() + request.getParams().size()];
            int index = 0;
            for (FileParts fileParts : request.getFileParts()) {
                try {
                    parts[index++] = new FilePart(fileParts.getName(), fileParts.getFilePart());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(String.format("文件不存在：%s[%s]", fileParts.getName(),
                            fileParts.getFilePart().getAbsolutePath()), e);
                }
            }
            for (NameValuePair nameValuePair : request.getParams()) {
                parts[index++] = new StringPart(nameValuePair.getName(), nameValuePair.getValue(), request.getCharset());
            }
            method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
        }

        return executeMethod(method, request);
    }

    private Response executeMethod(HttpMethodBase httpMethod, Request request) {
        try {
            long startTime = System.currentTimeMillis();
            int status = httpClient.executeMethod(httpMethod);
            long endTime = System.currentTimeMillis();

            Response response = new Response();
            response.setRequest(request);
            response.setStatus(status);
            response.setBody(Okio.buffer(Okio.source(httpMethod.getResponseBodyAsStream())).readByteArray());
            Stream.of(httpMethod.getResponseHeaders()).forEach(h -> response.getHeaders().add(new NameValuePair(h.getName(), h.getValue())));

            //log.info(buildConnectionInfo(startTime, endTime, status, httpMethod).toChromeStyleLog());
            return response;
        } catch (IOException e) {
            throw new RuntimeException("HTTP请求失败", e);
        } finally {
            httpMethod.releaseConnection();
        }
    }

    private ConnectionInfo buildConnectionInfo(long startTime, long endTime, int status, HttpMethodBase httpMethod) {
        try {
            ConnectionInfo connectionInfo = ConnectionInfo.builder()
                    .url(httpMethod.getURI().getURI())
                    .statusCode(status)
                    .statusLine(httpMethod.getStatusLine().toString())
                    .statusText(httpMethod.getStatusText())
                    .requestHeaders(Arrays.stream(httpMethod.getRequestHeaders()).map(d -> new NameValuePair(d.getName(), d.getValue())).collect(Collectors.toList()))
                    .responseHeaders(Arrays.stream(httpMethod.getResponseHeaders()).map(d -> new NameValuePair(d.getName(), d.getValue())).collect(Collectors.toList()))
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();

            if (httpMethod instanceof GetMethod) {
                connectionInfo.setMethod("GET");
                if (StringUtils.isNotBlank(httpMethod.getQueryString())) {
                    Stream.of(httpMethod.getQueryString().split("&")).forEach(s -> connectionInfo.getParams().add(new NameValuePair(s.split("=")[0], s.split("=")[1])));
                }
            }
            if (httpMethod instanceof PostMethod) {
                PostMethod postMethod = (PostMethod) httpMethod;
                connectionInfo.setMethod("POST");
                RequestEntity requestEntity = postMethod.getRequestEntity();
                if (requestEntity instanceof StringRequestEntity) {
                    connectionInfo.setStringBody(((StringRequestEntity) requestEntity).getContent());
                }
                Stream.of(postMethod.getParameters()).forEach(h -> connectionInfo.getParams().add(new NameValuePair(h.getName(), h.getValue())));
            }
            return connectionInfo;
        } catch (URIException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    public void addCookie(Cookie cookie) {
//        httpClient.getState().addCookie(CookieUtils.copyProperties(cookie, new org.apache.commons.httpclient.Cookie()));
//    }
//
//    @Override
//    public List<Cookie> getCookies() {
//        return Arrays.stream(httpClient.getState().getCookies()).map(c -> CookieUtils.copyProperties(c, new Cookie())).collect(Collectors.toList());
//    }
//
//    @Override
//    public void clearCookies() {
//        httpClient.getState()
//        httpClient.getState().clearCookies();
//    }
//
//    @Override
//    public void addCookies(List<Cookie> cookie) {
//        if (CollectionUtils.isEmpty(cookie)) {
//            return;
//        }
//        httpClient.getState().addCookies(cookie.stream().map(d -> CookieUtils.copyProperties(cookie, new org.apache.commons.httpclient.Cookie())).toArray(org.apache.commons.httpclient.Cookie[]::new));
//    }

}
