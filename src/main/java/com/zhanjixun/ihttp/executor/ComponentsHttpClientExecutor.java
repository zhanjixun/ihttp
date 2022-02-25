package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.cookie.Cookie;
import com.zhanjixun.ihttp.domain.FormDatas;
import com.zhanjixun.ihttp.domain.Header;
import com.zhanjixun.ihttp.domain.MultipartFile;
import com.zhanjixun.ihttp.domain.Param;
import com.zhanjixun.ihttp.parsing.Configuration;
import com.zhanjixun.ihttp.parsing.HttpProxy;
import com.zhanjixun.ihttp.utils.StrUtils;
import com.zhanjixun.ihttp.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author :zhanjixun
 * @date : 2018/10/9 16:30
 */
@Slf4j
public class ComponentsHttpClientExecutor extends BaseExecutor {

    private final HttpClient httpClient;

    public ComponentsHttpClientExecutor(Configuration configuration, CookiesStore cookiesStore) {
        super(configuration, cookiesStore);
        HttpClientBuilder builder = HttpClients.custom();
        //cookie
        builder.setDefaultCookieStore(new MyCookieStore(cookiesStore));
        //代理
        HttpProxy proxy = configuration.getProxy();
        if (proxy != null) {
            builder.setProxy(new HttpHost(proxy.getHostName(), proxy.getPort()));
            try {
                builder.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> proxy.isTrustSSL()).build());
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                e.printStackTrace();
            }
        }
        httpClient = builder.build();
    }

    @Override
    protected Response executeRequest(Request request) throws IOException {
        String method = request.getMethod();
        if (Arrays.asList(new String[]{"POST", "PUT", "PATCH"}).contains(method)) {
            return executeMethod(buildEntityRequest(request, new HttpPost()), request);
        }
        if (Arrays.asList(new String[]{"GET", "DELETE", "TRACE", "OPTIONS", "HEAD"}).contains(method)) {
            return executeMethod(buildRequestBase(request, new HttpPost()), request);
        }
        return null;
    }

    private HttpRequestBase buildRequestBase(Request request, HttpRequestBase method) {
        request.setUrl(StrUtils.addQuery(request.getUrl(), request.getParams()));
        request.getParams().clear();

        method.setURI(URI.create(request.getUrl()));
        method.setConfig(RequestConfig.custom().setRedirectsEnabled(request.getFollowRedirects()).build());
        request.getHeaders().forEach(h -> method.addHeader(h.getName(), h.getValue()));
        return method;
    }

    private HttpEntityEnclosingRequestBase buildEntityRequest(Request request, HttpEntityEnclosingRequestBase method) {
        method.setURI(URI.create(request.getUrl()));
        request.getHeaders().forEach(h -> method.addHeader(h.getName(), h.getValue()));

        String charset = Optional.ofNullable(request.getCharset()).orElse("UTF-8");
        String contentType = Optional.ofNullable(method.getFirstHeader("Content-Type")).map(org.apache.http.NameValuePair::getValue).orElse(null);
        //带参数
        String paramString = request.getParams().stream().map(p -> p.getName() + "=" + p.getValue()).collect(Collectors.joining("&"));
        if (Util.isNotBlank(paramString)) {
            ContentType contentTypeDefault = ContentType.create("application/x-www-form-urlencoded", charset);
            method.setEntity(new StringEntity(paramString, Optional.ofNullable(contentType).map(ContentType::create).orElse(contentTypeDefault)));
        }
        //直接请求体
        if (Util.isNotBlank(request.getBody())) {
            ContentType type = contentType != null ? ContentType.create(contentType) : ContentType.APPLICATION_JSON;
            method.setEntity(new StringEntity(request.getBody(), type));
        }
        //带文件
        if (Util.isNotEmpty(request.getFileParts())) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (Param param : request.getParams()) {
                builder.addTextBody(param.getName(), param.getValue());
            }
            for (FormDatas parts : request.getFileParts()) {
                MultipartFile formData = parts.getFormData();
                ContentType type = (formData.getContentType() != null) ? ContentType.create(formData.getContentType()) : ContentType.DEFAULT_BINARY;
                builder.addBinaryBody(parts.getName(), formData.getData(), type, formData.getFileName());
            }
            method.setEntity(builder.build());
        }
        return method;
    }

    private Response executeMethod(HttpRequestBase method, Request request) throws IOException {
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(method);

            Map<String, List<String>> headers = Arrays.stream(httpResponse.getAllHeaders()).map(h -> new Header(h.getName(), h.getValue()))
                    .collect(Collectors.toMap(Header::getName, h -> Collections.singletonList(h.getValue()),
                            (a, b) -> Stream.concat(a.stream(), b.stream()).collect(Collectors.toList())));

            Response response = new Response();
            response.setRequest(request);
            response.setCharset(headers.getOrDefault("Content-Type", new ArrayList<>()).stream().filter(h -> h.contains("charset=") && !h.endsWith("charset=")).map(h -> h.substring(h.lastIndexOf("charset=") + "charset=".length())).findFirst().orElse("UTF-8"));
            response.setStatus(httpResponse.getStatusLine().getStatusCode());
            response.setBody(EntityUtils.toByteArray(httpResponse.getEntity()));
            response.setHeaders(headers);
            return response;
        } finally {
            if (httpResponse != null && httpResponse.getEntity() != null) {
                try {
                    //不关闭流会导致阻塞
                    InputStream content = httpResponse.getEntity().getContent();
                    if (content != null) {
                        content.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class MyCookieStore implements CookieStore {

        private final CookiesStore cookiesStore;

        MyCookieStore(CookiesStore cookiesStore) {
            this.cookiesStore = cookiesStore;
        }

        @Override
        public void addCookie(org.apache.http.cookie.Cookie originCookie) {
            cookiesStore.addCookie(Cookie.builder()
                    .name(originCookie.getName())
                    .value(originCookie.getValue())
                    .domain(originCookie.getDomain())
                    .path(originCookie.getPath())
                    .expiryDate(originCookie.getExpiryDate())
                    .comment(originCookie.getComment())
                    .isSecure(originCookie.isSecure())
                    .version(originCookie.getVersion())
                    .build());
        }

        @Override
        public List<org.apache.http.cookie.Cookie> getCookies() {
            return cookiesStore.getCookies().stream().map(originCookie -> {
                BasicClientCookie target = new BasicClientCookie(originCookie.getName(), originCookie.getValue());
                target.setDomain(originCookie.getDomain());
                target.setPath(originCookie.getPath());
                target.setExpiryDate(originCookie.getExpiryDate());
                target.setComment(originCookie.getComment());
                target.setSecure(originCookie.isSecure());
                target.setVersion(originCookie.getVersion());
                return target;
            }).collect(Collectors.toList());
        }

        @Override
        public boolean clearExpired(Date date) {
            return cookiesStore.clearExpired(date);
        }

        @Override
        public void clear() {
            cookiesStore.clearCookies();
        }
    }

}
