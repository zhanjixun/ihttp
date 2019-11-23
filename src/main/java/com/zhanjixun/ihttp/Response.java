package com.zhanjixun.ihttp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhanjixun.ihttp.domain.Header;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

	private List<Header> headers = new ArrayList<>();

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
				text = new String(body, charset);
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
