package com.zhanjixun.ihttp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * http请求结果
 *
 * @author zhanjixun
 */
@Data
@NoArgsConstructor
public class Response implements Serializable {

    private static final long serialVersionUID = -4834694151773821099L;
    /**
     * 执行的请求
     */
    private Request request;
    /**
     * 返回状态码
     */
    private int status;
    /**
     * 返回请求头
     */
    private Map<String, List<String>> headers;
    /**
     * 返回头：内容类型
     */
    private String contentType;
    /**
     * 返回头：重定向位置
     */
    private String location;
    /**
     * 返回值正文
     */
    private byte[] body;
    /**
     * 返回值正文字符编码
     */
    private String charset;
    /**
     * 返回值正文
     */
    private String text;
    /**
     * 处理后的结果
     */
    private Object data;
    /**
     * 处理结果
     */
    private Supplier<Object> handleSupplier;

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        //抽取出一些常用的请求头
        contentType = Optional.ofNullable(headers.get("Content-Type")).map(hs -> hs.get(0)).orElse(null);
        location = Optional.ofNullable(headers.get("Location")).map(hs -> hs.get(0)).orElse(null);
    }

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

    /**
     * 获取处理后的结果
     *
     * @param <T>
     * @return
     */
    public <T> T getData() {
        if (data == null) {
            data = handleSupplier.get();
        }
        return (T) data;
    }

    @Override
    public String toString() {
        return request.getMethod() + " " + getStatus() + " " + request.getUrl();
    }
}