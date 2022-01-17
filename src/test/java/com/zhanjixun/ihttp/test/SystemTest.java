package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.URL;
import com.zhanjixun.ihttp.annotations.UserAgent;
import org.junit.Test;

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
        System.out.println(mapper.testGet().getText());
        System.out.println(mapper.testGet().getText());
    }

}
