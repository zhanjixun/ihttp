package com.zhanjixun.ihttp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * http请求结果
 *
 * @author zhanjixun
 */
@Data
public class Response {

    private int status;
    private Map<String, String> headers = Maps.newHashMap();//@bug 当头的key相同value不相同的时候就不能保留多个了！
    private byte[] body;
    private String charset;

    private String text;
    private Document document;
    private BufferedImage image;

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public boolean isOK() {
        return status == 200;
    }

    public void ifOK(Consumer<Response> ok) {
        if (isOK()) {
            ok.accept(this);
        }
    }

    public void ok(Consumer<Response> ok, Consumer<Response> no) {
        if (isOK()) {
            ok.accept(this);
        } else {
            no.accept(this);
        }
    }

    public boolean isRedirect() {
        return status == 302;
    }

    public boolean isNotFound() {
        return status == 404;
    }

    public String getText() {
        if (text == null) {
            try {
                text = new String(body, charset);
            } catch (UnsupportedEncodingException e) {
                throw new UnsupportedOperationException("响应类型不是文本");
            }
        }
        return text;
    }

    public Document getDocument() {
        if (document == null) {
            document = Jsoup.parse(getText());
        }
        return document;
    }

    public BufferedImage getImage() {
        if (image == null) {
            try {
                image = ImageIO.read(new ByteArrayInputStream(body));
            } catch (IOException e) {
                throw new UnsupportedOperationException("响应类型不是图片");
            }
        }
        return image;
    }

    public JSONObject toJSONObject() {
        return JSON.parseObject(getText());
    }

    public JSONArray toJSONArray() {
        return JSON.parseArray(getText());
    }

}
