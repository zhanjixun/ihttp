package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.URL;
import com.zhanjixun.ihttp.annotations.UserAgent;
import com.zhanjixun.ihttp.cookie.Cookie;
import com.zhanjixun.ihttp.handler.annotations.JsonPath;
import lombok.Data;
import org.junit.Test;

import java.util.List;

/**
 * @author zhanjixun
 * @date 2022-01-07 18:20
 */
public class HandlerTest {

    private final TestMapper mapper = IHTTP.getMapper(TestMapper.class);

    @UserAgent
    @URL("http://localhost:8088")
    public interface TestMapper extends CookiesStore {
        @GET
        @URL("/getListInfo")
        @JsonPath(path = "$.data", returnType = Item.class)
        Response getInfo();
    }

    @Data
    static class Item {
        private String id;
    }

    @Test
    public void name() {
        List<Item> data = mapper.getInfo().getData();
        System.out.println(data);
    }
}
