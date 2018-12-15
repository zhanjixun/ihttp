package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.domain.Cookie;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import com.zhanjixun.ihttp.utils.CookieUtils;
import com.zhanjixun.ihttp.utils.StrUtils;
import lombok.extern.log4j.Log4j;
import okio.Okio;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Java原生http接口
 *
 * @author :zhanjixun
 * @date : 2018/10/9 16:02
 */
@Log4j
public class JavaExecutor extends BaseExecutor {

    private final CookieManager cookieManager = new CookieManager();

    public JavaExecutor(Configuration configuration) {
        super(configuration);
        CookieHandler.setDefault(cookieManager);
    }


    @Override
    protected Response doPostMethod(Request request) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(request.getUrl()).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //请求头
            request.getHeaders().forEach(h -> connection.addRequestProperty(h.getName(), h.getValue()));

            //参数
            String paramString = request.getParams().stream().map(p -> p.getName() + "=" + p.getValue()).collect(Collectors.joining("&"));
            if (StringUtils.isNotBlank(paramString)) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(paramString.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            //发送JSON
            if (StringUtils.isNotBlank(request.getBody())) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(request.getBody().getBytes());
                outputStream.flush();
                outputStream.close();
            }
            //文件上传
            if (CollectionUtils.isNotEmpty(request.getFileParts())) {
                OutputStream outputStream = connection.getOutputStream();
                String prefix = "----", boundary = UUID.randomUUID().toString(), lineEnd = "\r\n";
                String oneLine = prefix + boundary + lineEnd;

                connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + prefix + boundary);

                for (NameValuePair nameValuePair : request.getParams()) {
                    outputStream.write(oneLine.getBytes());
                    outputStream.write(String.format("Content-Disposition: form-data; name=\"%s\"%s", nameValuePair.getName(), lineEnd).getBytes());

                    outputStream.write(lineEnd.getBytes());
                    outputStream.write(String.format("%s%s", nameValuePair.getValue(), lineEnd).getBytes());
                }
                for (FileParts fileParts : request.getFileParts()) {
                    String mimeType = new MimetypesFileTypeMap().getContentType(fileParts.getFilePart());
                    String contentType = Optional.ofNullable(mimeType).orElse("application/octet-stream");

                    outputStream.write(oneLine.getBytes());
                    outputStream.write(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", fileParts.getName(), fileParts.getFilePart().getName()).getBytes());
                    outputStream.write(String.format("Content-Type: %s%s", contentType, lineEnd).getBytes());
                    outputStream.write(lineEnd.getBytes());

                    outputStream.write(Okio.buffer(Okio.source(fileParts.getFilePart())).readByteArray());
                }
                outputStream.write(oneLine.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            return executeMethod(request, connection);
        } catch (IOException e) {
            throw new RuntimeException("构建POST请求失败", e);
        }
    }

    @Override
    protected Response doGetMethod(Request request) {
        try {
            URL url = new URL(StrUtils.addQuery(request.getUrl(), request.getParams()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(request.isFollowRedirects());

            connection.setDoOutput(false);
            connection.setDoInput(true);

            request.getHeaders().forEach(h -> connection.addRequestProperty(h.getName(), h.getValue()));
            return executeMethod(request, connection);
        } catch (MalformedURLException e) {
            throw new RuntimeException("构建URL失败" + request.getUrl(), e);
        } catch (ProtocolException e) {
            throw new RuntimeException("构建请求失败", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response executeMethod(Request request, HttpURLConnection connection) {
        try {
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            //发送请求
            connection.connect();

            Response response = new Response();
            response.setRequest(request);
            response.setStatus(connection.getResponseCode());
            //返回请求头
            connection.getHeaderFields().entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream().map(value -> new NameValuePair(entry.getKey(), value)))
                    .forEach(h -> response.getHeaders().add(h));

            response.setBody(Okio.buffer(Okio.source(connection.getInputStream())).readByteArray());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("发送http请求失败", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
        try {
            cookieManager.getCookieStore().add(new URI(cookie.getDomain()), CookieUtils.copyProperties(cookie, new HttpCookie(cookie.getName(), cookie.getValue())));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cookie> getCookies() {
        return cookieManager.getCookieStore().getCookies().stream().map(d -> CookieUtils.copyProperties(d, new Cookie())).collect(Collectors.toList());
    }

    @Override
    public void clearCookies() {
        cookieManager.getCookieStore().removeAll();
    }

    @Override
    public void addCookies(List<Cookie> cookie) {
        cookie.forEach(this::addCookie);
    }
}
