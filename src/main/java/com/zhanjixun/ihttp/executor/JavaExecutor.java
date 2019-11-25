package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.domain.FormData;
import com.zhanjixun.ihttp.domain.Header;
import com.zhanjixun.ihttp.domain.Param;
import com.zhanjixun.ihttp.parsing.Configuration;
import com.zhanjixun.ihttp.utils.StrUtils;
import com.zhanjixun.ihttp.utils.Util;
import lombok.extern.slf4j.Slf4j;
import okio.Okio;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Java原生http接口
 *
 * @author :zhanjixun
 * @date : 2018/10/9 16:02
 */
@Slf4j
public class JavaExecutor extends BaseExecutor {

    public JavaExecutor(Configuration configuration, CookiesStore cookiesStore) {
        super(configuration, cookiesStore);
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    protected Response doGetMethod(Request request) throws IOException {
        URL url = new URL(StrUtils.addQuery(request.getUrl(), request.getParams()));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(request.getFollowRedirects());

        connection.setDoOutput(false);
        connection.setDoInput(true);

        request.getHeaders().forEach(h -> connection.addRequestProperty(h.getName(), h.getValue()));
        return executeMethod(request, connection);
    }

    @Override
    protected Response doPostMethod(Request request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(request.getUrl()).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        //请求头
        request.getHeaders().forEach(h -> connection.addRequestProperty(h.getName(), h.getValue()));

        //参数
        String paramString = request.getParams().stream().map(p -> p.getName() + "=" + p.getValue()).collect(Collectors.joining("&"));
        if (Util.isNotBlank(paramString)) {
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(paramString.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        //发送JSON
        if (Util.isNotBlank(request.getBody())) {
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(request.getBody().getBytes());
            outputStream.flush();
            outputStream.close();
        }
        //文件上传
        if (Util.isNotEmpty(request.getFileParts())) {
            OutputStream outputStream = connection.getOutputStream();
            String prefix = "----", boundary = UUID.randomUUID().toString(), lineEnd = "\r\n";
            String oneLine = prefix + boundary + lineEnd;

            connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + prefix + boundary);

            for (Param nameValuePair : request.getParams()) {
                outputStream.write(oneLine.getBytes());
                outputStream.write(String.format("Content-Disposition: form-data; name=\"%s\"%s", nameValuePair.getName(), lineEnd).getBytes());

                outputStream.write(lineEnd.getBytes());
                outputStream.write(String.format("%s%s", nameValuePair.getValue(), lineEnd).getBytes());
            }
            for (FormData formData : request.getFileParts()) {
                String mimeType = new MimetypesFileTypeMap().getContentType(formData.getFilePart());
                String contentType = Optional.ofNullable(mimeType).orElse("application/octet-stream");

                outputStream.write(oneLine.getBytes());
                outputStream.write(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", formData.getName(), formData.getFilePart().getName()).getBytes());
                outputStream.write(String.format("Content-Type: %s%s", contentType, lineEnd).getBytes());
                outputStream.write(lineEnd.getBytes());

                outputStream.write(Okio.buffer(Okio.source(formData.getFilePart())).readByteArray());
            }
            outputStream.write(oneLine.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        return executeMethod(request, connection);
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


    private Response executeMethod(Request request, HttpURLConnection connection) throws IOException {
        try {
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            //发送请求
            connection.connect();

            Response response = new Response();
            response.setRequest(request);
            response.setCharset(request.getResponseCharset());
            response.setStatus(connection.getResponseCode());
            //返回请求头
            connection.getHeaderFields().entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream().map(value -> new Header(entry.getKey(), value)))
                    .forEach(h -> response.getHeaders().add(h));

            response.setBody(Okio.buffer(Okio.source(connection.getInputStream())).readByteArray());
            return response;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

//	@Override
//	protected Response doPutMethod(Request request) throws IOException {
//		return null;
//	}
//
//	@Override
//	protected Response doPatchMethod(Request request) throws IOException {
//		return null;
//	}
//
//	@Override
//	protected Response doTraceMethod(Request request) throws IOException {
//		return null;
//	}
//
//	@Override
//	protected Response doOptionsMethod(Request request) throws IOException {
//		return null;
//	}
//
//	@Override
//	protected Response doHeadMethod(Request request) throws IOException {
//		return null;
//	}
//
//
//	private Response executeMethod(Request request, HttpURLConnection connection) throws IOException {
//		try {
//			connection.setConnectTimeout(3000);
//			connection.setReadTimeout(3000);
//			//发送请求
//			connection.connect();
//
//			Response response = new Response();
//			response.setRequest(request);
//			response.setCharset(request.getResponseCharset());
//			response.setStatus(connection.getResponseCode());
//			//返回请求头
//			connection.getHeaderFields().entrySet().stream()
//					.flatMap(entry -> entry.getValue().stream().map(value -> new Header(entry.getKey(), value)))
//					.forEach(h -> response.getHeaders().add(h));
//
//			response.setBody(Okio.buffer(Okio.source(connection.getInputStream())).readByteArray());
//			return response;
//		} finally {
//			if (connection != null) {
//				connection.disconnect();
//			}
//		}
//	}
//
////    @Override
////    public void addCookie(Cookie cookie) {
////        try {
////            cookieManager.getCookieStore().add(new URI(cookie.getDomain()), CookieUtils.copyProperties(cookie, new HttpCookie(cookie.getName(), cookie.getValue())));
////        } catch (URISyntaxException e) {
////            e.printStackTrace();
////        }
////    }
////
////    @Override
////    public List<Cookie> getCookies() {
////        return cookieManager.getCookieStore().getCookies().stream().map(d -> CookieUtils.copyProperties(d, new Cookie())).collect(Collectors.toList());
////    }
////
////    @Override
////    public void clearCookies() {
////        cookieManager.getCookieStore().removeAll();
////    }
////
////    @Override
////    public void addCookies(List<Cookie> cookie) {
////        cookie.forEach(this::addCookie);
////    }
//}
