package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.constant.Config;
import com.zhanjixun.ihttp.logging.ConnectionInfo;
import com.zhanjixun.ihttp.logging.Log;
import lombok.extern.log4j.Log4j;
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
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基于org.apache.commons.httpclient.HttpClient的http框架
 *
 * @author zhanjixun
 * @see HttpClient
 */
@Log4j
public class HttpClientExecutor extends BaseExecutor {

    private final HttpClient client = new HttpClient();
    private Log logger;

    public HttpClientExecutor(Config config) {
        if (config != null) {
            if (config.getProxy() != null) {
                client.getHostConfiguration().setProxy(config.getProxy().hostName(), config.getProxy().port());
            }
            if (config.getLogger() != null) {
                try {
                    logger = config.getLogger().value().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected Response doGetMethod(Request request) {
        GetMethod method = new GetMethod(request.getUrl());
        String charset = Optional.ofNullable(request.getCharset()).orElse("UTF-8");
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);

        Set<String> queryString = request.getParams().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.toSet());

        if (StringUtils.isNotEmpty(method.getQueryString())) {
            queryString.add(method.getQueryString());
        }
        if (CollectionUtils.isNotEmpty(queryString)) {
            method.setQueryString(String.join("&", queryString));
        }
        request.getHeaders().forEach(method::addRequestHeader);

        return executeMethod(method, request.getResponseCharset());
    }

    @Override
    public Response doPostMethod(Request request) {
        PostMethod method = new PostMethod(request.getUrl());
        String charset = Optional.ofNullable(request.getCharset()).orElse("UTF-8");
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);

        request.getHeaders().forEach(method::addRequestHeader);
        request.getParams().forEach(method::addParameter);

        if (!request.getFiles().isEmpty()) {
            Part[] parts = new Part[request.getFiles().size() + request.getParams().size()];
            int i = 0;
            for (Map.Entry<String, File> entry : request.getFiles().entrySet()) {
                try {
                    parts[i++] = new FilePart(entry.getKey(), entry.getValue());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<String, String> entry : request.getParams().entrySet()) {
                parts[i++] = new StringPart(entry.getKey(), entry.getValue(), charset);
            }
            method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
        }

        if (request.getBody() != null) {
            String contentType = Optional.ofNullable(method.getRequestHeader("Content-Type"))
                    .orElse(new Header("", "text/html"))
                    .getValue();
            try {
                method.setRequestEntity(new StringRequestEntity(request.getBody(), contentType, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return executeMethod(method, request.getResponseCharset());
    }

    private Response executeMethod(HttpMethodBase httpMethod, String customizedResponseCharset) {
        try {
            long startTime = System.currentTimeMillis();
            int status = client.executeMethod(httpMethod);
            ConnectionInfo connectionInfo = buildConnectionInfo(startTime, System.currentTimeMillis(), status, httpMethod);

            Response response = new Response();
            response.setStatus(status);
            response.setBody(httpMethod.getResponseBody());
            response.setCharset(Optional.ofNullable(customizedResponseCharset).orElse(httpMethod.getResponseCharSet()));
            Stream.of(httpMethod.getResponseHeaders()).forEach(header -> response.getHeaders().put(header.getName(), header.getValue()));

            if (logger != null) {
                log.info(logger.toLogString(connectionInfo));
            }
            return response;
        } catch (IOException e) {
            throw new RuntimeException("HTTP请求失败", e);
        } finally {
            // 释放连接
            httpMethod.releaseConnection();
        }
    }

    private ConnectionInfo buildConnectionInfo(long startTime, long endTime, int status, HttpMethodBase httpMethod) {
        try {
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setStartTime(startTime);
            connectionInfo.setEndTime(endTime);
            connectionInfo.setUrl(httpMethod.getURI().getURI());
            connectionInfo.setStatusCode(status);
            connectionInfo.setStatusLine(httpMethod.getStatusLine().toString());
            connectionInfo.setStatusText(httpMethod.getStatusText());

            Stream.of(httpMethod.getRequestHeaders()).forEach(h -> connectionInfo.getRequestHeaders().put(h.getName(), h.getValue()));
            Stream.of(httpMethod.getResponseHeaders()).forEach(h -> connectionInfo.getResponseHeaders().put(h.getName(), h.getValue()));

            if (httpMethod instanceof GetMethod) {
                connectionInfo.setMethod("GET");
                if (httpMethod.getQueryString() != null) {
                    Stream.of(httpMethod.getQueryString().split("&")).forEach(s -> connectionInfo.getParams().put(s.split("=")[0], s.split("=")[1]));
                }
            }
            if (httpMethod instanceof PostMethod) {
                PostMethod postMethod = (PostMethod) httpMethod;
                connectionInfo.setMethod("POST");
                RequestEntity requestEntity = postMethod.getRequestEntity();
                if (requestEntity != null && requestEntity instanceof StringRequestEntity) {
                    connectionInfo.setStringBody(((StringRequestEntity) requestEntity).getContent());
                }
                Stream.of(postMethod.getParameters()).forEach(h -> connectionInfo.getParams().put(h.getName(), h.getValue()));
            }
            return connectionInfo;
        } catch (URIException e) {
            e.printStackTrace();
        }
        return null;
    }

}
