package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.URL;
import com.zhanjixun.ihttp.annotations.UserAgent;
import com.zhanjixun.ihttp.cookie.Cookie;
import org.junit.Test;

import java.util.List;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SystemTest extends BaseTest {

    private final TestMapper mapper = IHTTP.getMapper(TestMapper.class);

    @UserAgent
    @URL("http://localhost:8088")
    public interface TestMapper extends CookiesStore {

        @GET
        @URL("/echo")
        Response testGet();

    }

    @Test
    public void name() {
        Response response = mapper.testGet();
        assert response.getStatus() == 200;
        System.out.println(response.getText());
        List<Cookie> cookies = mapper.getCookies();
        System.out.println();
    }

}
