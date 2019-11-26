package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.domain.FormData;
import com.zhanjixun.ihttp.domain.FormDatas;
import com.zhanjixun.ihttp.domain.Header;
import com.zhanjixun.ihttp.parsing.Configuration;
import com.zhanjixun.ihttp.parsing.HttpProxy;
import com.zhanjixun.ihttp.utils.CookieUtils;
import com.zhanjixun.ihttp.utils.StrUtils;
import com.zhanjixun.ihttp.utils.Util;
import lombok.extern.slf4j.Slf4j;
import okio.Okio;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	protected Response doGetMethod(Request request) throws IOException {
		return executeMethod(buildRequestBase(request, new HttpGet()), request);
	}

	@Override
	protected Response doPostMethod(Request request) throws IOException {
		return executeMethod(buildEntityRequest(request, new HttpPost()), request);
	}

	@Override
	protected Response doDeleteMethod(Request request) throws IOException {
		return executeMethod(buildRequestBase(request, new HttpDelete()), request);
	}

	@Override
	protected Response doPutMethod(Request request) throws IOException {
		return executeMethod(buildEntityRequest(request, new HttpPut()), request);
	}

	@Override
	protected Response doPatchMethod(Request request) throws IOException {
		return executeMethod(buildEntityRequest(request, new HttpPatch()), request);
	}

	@Override
	protected Response doTraceMethod(Request request) throws IOException {
		return executeMethod(buildRequestBase(request, new HttpTrace()), request);
	}

	@Override
	protected Response doOptionsMethod(Request request) throws IOException {
		return executeMethod(buildRequestBase(request, new HttpOptions()), request);
	}

	@Override
	protected Response doHeadMethod(Request request) throws IOException {
		return executeMethod(buildRequestBase(request, new HttpHead()), request);
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
			method.setEntity(new StringEntity(paramString, contentType != null ? ContentType.create(contentType) : contentTypeDefault));
		}
		//直接请求体
		if (Util.isNotBlank(request.getBody())) {
			ContentType type = contentType != null ? ContentType.create(contentType) : ContentType.APPLICATION_JSON;
			method.setEntity(new StringEntity(request.getBody(), type));
		}
		//带文件
		if (Util.isNotEmpty(request.getFileParts())) {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			request.getParams().forEach(p -> builder.addTextBody(p.getName(), p.getValue()));

			for (FormDatas parts : request.getFileParts()) {
				FormData formData = parts.getFormData();
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
			List<Header> headers = Arrays.stream(httpResponse.getAllHeaders()).map(h -> new Header(h.getName(), h.getValue())).collect(Collectors.toList());

			String charset = request.getResponseCharset();
			if (charset == null) {
				Optional<String> contentType = headers.stream().filter(h -> "Content-Type".equalsIgnoreCase(h.getName())).map(Header::getValue).findFirst();
				if (contentType.isPresent()) {
					Matcher matcher = Pattern.compile("charset=(.*)$").matcher(contentType.get());
					charset = matcher.find() ? matcher.group(1) : "UTF-8";
				}
			}

			Response response = new Response();
			response.setRequest(request);
			response.setCharset(charset);
			response.setStatus(httpResponse.getStatusLine().getStatusCode());
			response.setBody(Okio.buffer(Okio.source(httpResponse.getEntity().getContent())).readByteArray());
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
