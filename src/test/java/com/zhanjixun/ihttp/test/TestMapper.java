package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.*;

import java.io.File;
import java.util.Map;


@URL("http://localhost:8088")
@UserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
public interface TestMapper {

    @GET
    @URL("/index")
    @RequestParam(name = "key1", value = "value1")
    @RequestParam(name = "key2", value = "value2")
    Response testGet(@RequestParam(name = "key3") String value3,
                     @RequestParam(name = "key4") int value4,
                     @RequestParam Map<String, Object> mapParam,
                     @RequestParam(name = "bean") Student beanParam);

    @POST
    @URL("/home")
    Response testPost(@RequestBody String body);

    @PUT
    @URL("/home")
    Response testPut(@RequestBody Map<String, Object> body);

    @POST
    @URL("/upload")
    @UserAgent("zhanjixun@qq.com")
    Response upload(@RequestPart(name = "file") File file);


    @DELETE
    @URL("/user/#{userId}")
    Response testDel(@Placeholder("userId") int userId);

}