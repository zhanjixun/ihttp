package com.zhanjixun.ihttp.logging;

import com.google.common.collect.Maps;
import lombok.Data;

import java.io.File;
import java.util.Map;

/**
 * 连接状态 代表一次http请求的各个情况
 *
 * @author zhanjixun
 */
@Data
public class ConnectionInfo {

    private String url;
    private String method;
    private String stringBody;

    private int statusCode;
    private String statusText;
    private String statusLine;

    private Map<String, String> params = Maps.newIdentityHashMap();
    private Map<String, File> files = Maps.newIdentityHashMap();

    private Map<String, String> requestHeaders = Maps.newIdentityHashMap();
    private Map<String, String> responseHeaders = Maps.newIdentityHashMap();

    private long startTime;
    private long endTime;

}
