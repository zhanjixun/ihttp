package com.zhanjixun.ihttp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.zhanjixun.ihttp.domain.NameValuePair;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.Consumer;

/**
 * http请求结果
 *
 * @author zhanjixun
 */
@Data
public class Response {

    private Request request;

    private int status;
    private List<NameValuePair> headers = Lists.newArrayList();

    private byte[] body;
    private String charset;

    private String text;
    private Document document;
    private BufferedImage image;

    public void addHeader(String name, String value) {
        headers.add(new NameValuePair(name, value));
    }

    public boolean isOK() {
        return status == 200;
    }

    public boolean isRedirect() {
        return status == 302;
    }

    public boolean isNotFound() {
        return status == 404;
    }

    public void ifStatus(int status, Consumer<Response> success) {
        if (this.status == status) {
            success.accept(this);
        }
    }

    public void ifStatus(int status, Consumer<Response> success, Consumer<Response> fail) {
        if (this.status == status) {
            success.accept(this);
        } else {
            fail.accept(this);
        }
    }

    public void ok(Consumer<Response> success) {
        ifStatus(200, success);
    }

    public void ok(Consumer<Response> success, Consumer<Response> fail) {
        ifStatus(200, success, fail);
    }

    public void redirect(Consumer<Response> success) {
        ifStatus(302, success);
    }

    public void redirect(Consumer<Response> success, Consumer<Response> fail) {
        ifStatus(302, success, fail);
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
