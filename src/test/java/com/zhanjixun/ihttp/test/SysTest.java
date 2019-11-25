package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.URL;
import com.zhanjixun.ihttp.annotations.UserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SysTest {

    private Hao6vMapper mapper = IHTTP.getMapper(Hao6vMapper.class);

    @Test
    public void name() {
        Response index = mapper.index();
        Document parse = Jsoup.parse(index.getText());
    }

    @URL("http://localhost:8088")
    interface Hao6vMapper {

        @GET
        @URL("/index.html")
        @UserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
        Response index();

    }
}
