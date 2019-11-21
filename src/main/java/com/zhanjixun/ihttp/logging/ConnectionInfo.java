package com.zhanjixun.ihttp.logging;

import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.POST;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import com.zhanjixun.ihttp.utils.Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.httpclient.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 连接状态 代表一次http请求的各个情况
 *
 * @author zhanjixun
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionInfo {

	private String url;
	private String method;
	private String version;

	private String stringBody;
	private int statusCode;
	private String statusText;
	private String statusLine;

	//请求
	private List<NameValuePair> requestHeaders = new ArrayList<>();
	private List<NameValuePair> params = new ArrayList<>();
	private List<FileParts> files = new ArrayList<>();

	//结果
	private List<NameValuePair> responseHeaders = new ArrayList<>();

	private long startTime;
	private long endTime;

	public String toChromeStyleLog() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("----------------------%s\n", formatDate(new Date(getStartTime()))));
		builder.append("▼ General\n");
		builder.append(String.format("Request URL:%s\n", getUrl()));
		builder.append(String.format("Request Method:%s\n", getMethod()));
		builder.append(String.format("Status Code:%d %s\n", getStatusCode(), getStatusText()));
		builder.append("\n");

		builder.append("▼ Request Headers\n");
		getRequestHeaders().forEach(h -> builder.append(String.format("%s : %s\n", h.getName(), h.getValue())));
		builder.append("\n");

		builder.append("▼ Response Headers" + "\n");
		getResponseHeaders().forEach(h -> builder.append(String.format("%s : %s\n", h.getName(), h.getValue())));
		builder.append("\n");

		if (GET.class.getSimpleName().equalsIgnoreCase(getMethod()) && Util.isNotEmpty(getParams())) {
			builder.append("▼ Query String Parameters\n");
			getParams().forEach(p -> builder.append(String.format("%s = %s\n", p.getName(), p.getValue())));
			builder.append("\n");
		}
		if (POST.class.getSimpleName().equalsIgnoreCase(getMethod()) && Util.isNotEmpty(getParams())) {
			builder.append("▼ Request Parameters\n");
			getParams().forEach(p -> builder.append(String.format("%s = %s\n", p.getName(), p.getValue())));
			builder.append("\n");
		}
		if (POST.class.getSimpleName().equalsIgnoreCase(getMethod()) && Objects.nonNull(getStringBody())) {
			builder.append("▼ Request Payload" + "\n");
			builder.append(getStringBody()).append("\n");
			builder.append("\n");
		}

		builder.append(String.format("----------------------%s", formatDate(new Date(getEndTime()))));
		builder.append(String.format("耗时：%dms\n", (getEndTime() - getStartTime())));
		return builder.toString();
	}

	private String formatDate(Date date) {
		return DateUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss.SSS");
	}

	public String toRawStyleLog() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s %s %s", method, url));
		return builder.toString();
	}

}
