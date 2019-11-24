package com.zhanjixun.ihttp.utils;

import com.zhanjixun.ihttp.domain.Param;

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

    public static String addQuery(String url, List<Param> params) {
        String queryString = params.stream().map(pair -> pair.getName() + "=" + pair.getValue()).collect(Collectors.joining("&"));
        if (Util.isNotBlank(queryString)) {
            url += url.contains("?") ? "&" + queryString : "?" + queryString;
        }
        return url;
    }
}
