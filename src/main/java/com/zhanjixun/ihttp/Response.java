package com.zhanjixun.ihttp;

import lombok.Data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * http请求结果
 *
 * @author zhanjixun
 */
@Data
public class Response implements Serializable {

    private static final long serialVersionUID = -4834694151773821099L;

    //执行的请求
    private Request request;

    //返回状态码
    private int status;

    //返回请求头
    private Map<String, List<String>> headers;
    /**
     * 返回头：内容类型
     */
    private String contentType;
    /**
     * 返回头：重定向位置
     */
    private String location;

    //返回值正文
    private byte[] body;

    //返回值正文字符编码
    private String charset;

    private String text;

    public String getText() {
        if (text == null) {
            try {
                text = new String(body, charset);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return text;
    }

    @Override
    public String toString() {
        return request.getMethod() + " " + getStatus() + " " + request.getUrl();
    }

}
