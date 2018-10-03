package com.zhanjixun.ihttp.logging;


import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.POST;
import org.apache.commons.httpclient.util.DateUtil;

import java.util.Date;

/**
 * Chrome风格的http日志
 *
 * @author zhanjixun
 * @date 2018年4月11日 23:48:03
 */
public class ChromeLog implements Log {

    @Override
    public String toLogString(ConnectionInfo info) {
        String dateFormatPattern = "yyyy-MM-dd HH:mm:ss.SSS";

        StringBuilder builder = new StringBuilder();
        builder.append("----------------------").append(DateUtil.formatDate(new Date(info.getStartTime()), dateFormatPattern)).append("\n");
        builder.append("▼ General").append("\n");
        builder.append("Request URL:").append(info.getUrl()).append("\n");
        builder.append("Request Method:").append(info.getMethod()).append("\n");
        builder.append("Status Code:").append(info.getStatusCode()).append(" ").append(info.getStatusText()).append("\n");
        builder.append("\n");

        builder.append("▼ Request Headers").append("\n");
        info.getRequestHeaders().forEach((k, v) -> builder.append(k).append(":").append(v).append("\n"));
        builder.append("\n");

        builder.append("▼ Response Headers" + "\n");
        info.getResponseHeaders().forEach((k, v) -> builder.append(k).append(":").append(v).append("\n"));
        builder.append("\n");

        if (GET.class.getSimpleName().equalsIgnoreCase(info.getMethod()) && !info.getParams().isEmpty()) {
            builder.append("▼ Query String Parameters" + "\n");
            info.getParams().forEach((k, v) -> builder.append(k).append("=").append(v).append("\n"));
            builder.append("\n");
        }
        if (POST.class.getSimpleName().equalsIgnoreCase(info.getMethod()) && !info.getParams().isEmpty()) {
            builder.append("▼ Request Parameters" + "\n");
            info.getParams().forEach((k, v) -> builder.append(k).append("=").append(v).append("\n"));
            builder.append("\n");
        }
        if (POST.class.getSimpleName().equalsIgnoreCase(info.getMethod()) && info.getStringBody() != null) {
            builder.append("▼ Request Payload" + "\n");
            builder.append(info.getStringBody()).append("\n");
            builder.append("\n");
        }

        builder.append("----------------------").append(DateUtil.formatDate(new Date(info.getEndTime()), dateFormatPattern)).append(" 耗时：" + (info.getEndTime() - info.getStartTime()) + "ms").append("\n");
        return builder.toString();
    }
}
