package com.zhanjixun.ihttp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.zhanjixun.ihttp.domain.NameValuePair;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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

    public boolean isOK() {
        return status == 200;
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
                if (StringUtils.isBlank(charset)) {
                    headers.stream().filter(h -> StringUtils.equals("Content-Type", h.getName()))
                            .map(NameValuePair::getValue).findFirst()
                            .ifPresent(s -> charset = StringUtils.substringAfterLast(s, "charset="));
                }
                text = new String(body, StringUtils.defaultIfBlank(charset, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new UnsupportedOperationException(e);
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
                throw new UnsupportedOperationException(e);
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
