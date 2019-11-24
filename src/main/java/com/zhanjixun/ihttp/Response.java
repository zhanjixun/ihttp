package com.zhanjixun.ihttp;

import com.zhanjixun.ihttp.domain.Header;
import lombok.Data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * http请求结果
 *
 * @author zhanjixun
 */
@Data
public class Response implements Serializable {

    private static final long serialVersionUID = -4834694151773821099L;

    private Request request;

    private int status;

    private List<Header> headers;

    private byte[] body;

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
