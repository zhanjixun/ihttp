package com.zhanjixun.ihttp;

import com.google.common.collect.Lists;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * http请求实体
 *
 * @author zhanjixun
 */
@Data
public class Request {

    private String id;
    //请求地址
    private String url;
    //请求编码
    private String method;
    //跟随重定向
    private boolean followRedirects = true;
    //请求体
    private String body;
    //请求字符编码
    private String charset;

    //请求头
    private List<NameValuePair> headers = Lists.newArrayList();
    //请求参数
    private List<NameValuePair> params = Lists.newArrayList();
    //文件
    private List<FileParts> fileParts = Lists.newArrayList();

    @Override
    public String toString() {
        return StringUtils.rightPad(getMethod(), 5) + " " + url;
    }
}
