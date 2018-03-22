package com.zhanjixun.ihttp;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * http请求结果
 *
 * @author zhanjixun
 */
public class Response {

    @Getter
    @Setter
    private int status;
    @Getter
    private Map<String, String> headers = Maps.newHashMap();
    @Getter
    @Setter
    private byte[] body;
    @Getter
    @Setter
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

    public boolean isRedirect() {
        return status == 302;
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
}
