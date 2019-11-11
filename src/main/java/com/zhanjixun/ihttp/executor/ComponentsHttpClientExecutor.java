package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.HttpProxy;
import com.zhanjixun.ihttp.domain.NameValuePair;
import com.zhanjixun.ihttp.utils.CookieUtils;
import com.zhanjixun.ihttp.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import okio.Okio;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
		if (configuration.getProxy() != null) {
			HttpProxy proxy = configuration.getProxy();
			builder.setProxy(new HttpHost(proxy.getHostName(), proxy.getPort()));
			try {
				builder.setSSLContext(new SSLContextBuilder()
						.loadTrustMaterial(null, (chain, authType) -> proxy.isTrustSSL())
						.build());
			} catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
				e.printStackTrace();
			}
		}
		httpClient = builder.build();
	}

	@Override
	protected Response doGetMethod(Request request) throws IOException {
		request.setUrl(StrUtils.addQuery(request.getUrl(), request.getParams()));
		request.getParams().clear();

		HttpGet method = new HttpGet(request.getUrl());
		method.setConfig(RequestConfig.custom().setRedirectsEnabled(request.isFollowRedirects()).build());
		request.getHeaders().forEach(h -> method.addHeader(h.getName(), h.getValue()));
		return executeMethod(method, request);
	}

	@Override
	protected Response doPostMethod(Request request) throws IOException {
		HttpPost method = new HttpPost(request.getUrl());
		request.getHeaders().forEach(h -> method.addHeader(h.getName(), h.getValue()));
		//带参数
		String charset = Optional.ofNullable(request.getCharset()).orElse("UTF-8");
		String paramString = request.getParams().stream().map(p -> p.getName() + "=" + p.getValue()).collect(Collectors.joining("&"));
		if (StringUtils.isNotBlank(paramString)) {
			method.setEntity(new StringEntity(paramString, ContentType.create("application/x-www-form-urlencoded", charset)));
		}
		//直接请求体
		if (StringUtils.isNotBlank(request.getBody())) {
			try {
				Optional<String> contentType = Optional.ofNullable(method.getFirstHeader("Content-Type")).map(org.apache.http.NameValuePair::getValue);
				if (contentType.isPresent()) {
					StringEntity stringEntity = new StringEntity(request.getBody());
					stringEntity.setContentType(contentType.get());
					method.setEntity(stringEntity);
				} else {
					method.setEntity(new StringEntity(request.getBody(), ContentType.APPLICATION_JSON));
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("构造请求体StringBody出错", e);
			}
		}
		//带文件
		if (CollectionUtils.isNotEmpty(request.getFileParts())) {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			request.getParams().forEach(p -> builder.addTextBody(p.getName(), p.getValue()));

			for (FileParts fileParts : request.getFileParts()) {
				File file = fileParts.getFilePart();

				String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
				ContentType contentType = mimeType != null ? ContentType.create(mimeType) : ContentType.DEFAULT_BINARY;

				builder.addBinaryBody(fileParts.getName(), file, contentType, file.getName());
			}
			method.setEntity(builder.build());
		}
		return executeMethod(method, request);
	}

	@Override
	protected Response doDeleteMethod(Request request) throws IOException {
		request.setUrl(StrUtils.addQuery(request.getUrl(), request.getParams()));
		request.getParams().clear();

		HttpDelete method = new HttpDelete(request.getUrl());
		method.setConfig(RequestConfig.custom().setRedirectsEnabled(request.isFollowRedirects()).build());
		request.getHeaders().forEach(h -> method.addHeader(h.getName(), h.getValue()));
		return executeMethod(method, request);
	}

	@Override
	protected Response doPutMethod(Request request) throws IOException {
		HttpPut method = new HttpPut(request.getUrl());
		request.getHeaders().forEach(h -> method.addHeader(h.getName(), h.getValue()));

		//带参数
		String charset = Optional.ofNullable(request.getCharset()).orElse("UTF-8");
		String paramString = request.getParams().stream().map(p -> p.getName() + "=" + p.getValue()).collect(Collectors.joining("&"));
		if (StringUtils.isNotBlank(paramString)) {
			method.setEntity(new StringEntity(paramString, ContentType.create("application/x-www-form-urlencoded", charset)));
		}
		//直接请求体
		if (StringUtils.isNotBlank(request.getBody())) {
			try {
				Optional<String> contentType = Optional.ofNullable(method.getFirstHeader("Content-Type")).map(org.apache.http.NameValuePair::getValue);
				if (contentType.isPresent()) {
					StringEntity stringEntity = new StringEntity(request.getBody());
					stringEntity.setContentType(contentType.get());
					method.setEntity(stringEntity);
				} else {
					method.setEntity(new StringEntity(request.getBody(), ContentType.APPLICATION_JSON));
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("构造请求体StringBody出错", e);
			}
		}
		//带文件
		if (CollectionUtils.isNotEmpty(request.getFileParts())) {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			request.getParams().forEach(p -> builder.addTextBody(p.getName(), p.getValue()));

			for (FileParts fileParts : request.getFileParts()) {
				File file = fileParts.getFilePart();

				String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
				ContentType contentType = mimeType != null ? ContentType.create(mimeType) : ContentType.DEFAULT_BINARY;

				builder.addBinaryBody(fileParts.getName(), file, contentType, file.getName());
			}
			method.setEntity(builder.build());
		}
		return executeMethod(method, request);
	}

	private Response executeMethod(HttpRequestBase method, Request request) throws IOException {
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(method);
			Response response = new Response();
			response.setRequest(request);
			response.setCharset(request.getResponseCharset());
			response.setStatus(httpResponse.getStatusLine().getStatusCode());
			response.setBody(Okio.buffer(Okio.source(httpResponse.getEntity().getContent())).readByteArray());
			Arrays.stream(httpResponse.getAllHeaders()).forEach(h -> response.getHeaders().add(new NameValuePair(h.getName(), h.getValue())));
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

	class MyCookieStore implements CookieStore {

		private CookiesStore cookiesStore;

		MyCookieStore(CookiesStore cookiesStore) {
			this.cookiesStore = cookiesStore;
		}

		@Override
		public void addCookie(org.apache.http.cookie.Cookie cookie) {
			cookiesStore.addCookie(CookieUtils.componentsConvert(cookie));
		}

		@Override
		public List<org.apache.http.cookie.Cookie> getCookies() {
			return cookiesStore.getCookies().stream().map(CookieUtils::componentsConvert).collect(Collectors.toList());
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
