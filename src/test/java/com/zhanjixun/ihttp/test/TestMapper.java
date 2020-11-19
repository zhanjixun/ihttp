package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.handler.annotations.CSSSelector;
import com.zhanjixun.ihttp.handler.annotations.JsonPath;
import com.zhanjixun.ihttp.handler.enums.ElementType;
import com.zhanjixun.ihttp.handler.enums.SelectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@URL("http://localhost:8088")
@UserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
public interface TestMapper {

    @GET
    @URL("/index")
    @RequestParam(name = "k1", value = "v1")
    @RequestParam(name = "k2", value = "v2")
    @RequestHeader(name = "token", value = "#{token}")
    @RandomPlaceholder(name = "token", length = 32)
    @TimestampRequestParam(name = "_")
    Response testGet(@RequestParam(name = "k3") String value3,
                     @RequestParam(name = "k4") int value4,
                     @RequestParam(name = "map") Map<String, Object> mapParam,
                     @RequestParam(name = "bean") Student beanParam);

    @POST
    @URL("/update")
    Response testPost(@RequestBody String body);

    @GET
    @URL("/student")
    @JsonPath(path = "$", returnType = Student.class)
    Response testJsonHandler();

    @GET
    @URL("/student2")
    @JsonPath(path = "$.data", returnType = Student.class)
    Response testJsonHandler2();

    @GET
    @URL("/home")
    @CSSSelector(selector = ".tips", selectType = SelectType.TEXT, returnType = String.class, elementType = ElementType.ARRAY)
    Response testHtmlHandler();

    @GET
    @URL("/home")
    @CSSSelector(selector = "#val", selectType = SelectType.ATTR, attr = "data-val", returnType = Integer.class)
    Response testHtmlHandler2();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Student {

        private String name;

        private int age;

        private String clazzName;

    }
}