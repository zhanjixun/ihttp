package com.zhanjixun.ihttp.utils;

import com.zhanjixun.ihttp.domain.NameValuePair;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author :zhanjixun
 * @date : 2018/11/29 10:33
 */
public class StrUtils {

    public static String URLEncoder(String text, String charset) {
        try {
            return URLEncoder.encode(text, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将参数添加到QueryString
     *
     * @param url
     * @param params
     * @return
     */
    public static String addQuery(String url, List<NameValuePair> params) {
        String queryString = params.stream().map(pair -> pair.getName() + "=" + pair.getValue()).collect(Collectors.joining("&"));
        if (StringUtils.isNotBlank(queryString)) {
            url += url.contains("?") ? "&" + queryString : "?" + queryString;
        }
        return url;
    }
}
