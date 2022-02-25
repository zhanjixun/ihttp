package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.PlaceholderManager;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.POST;
import com.zhanjixun.ihttp.annotations.RequestPart;
import com.zhanjixun.ihttp.annotations.URL;
import com.zhanjixun.ihttp.handler.annotations.JsonPath;
import lombok.Data;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SystemTest extends BaseTest {

    private final TestMapper mapper = IHTTP.getMapper(TestMapper.class);

    @URL("http://localhost:8088")
    //@Proxy(hostName = "localhost", port = 8888)
    public interface TestMapper extends CookiesStore, PlaceholderManager {
        @GET
        @URL("/echo")
        Response testGet();

        @GET
        @URL("/getListInfo")
        @JsonPath(path = "$.data", returnType = Item.class)
        Response getListInfo();

        @POST
        @URL("/uploadFile")
        @JsonPath(path = "$.content", returnType = String.class)
        Response uploadFile(@RequestPart(name = "file1") File file);
    }

    @Data
    static class Item {
        private String id;
        private String name;
    }

    @Test
    public void name1() {
        System.out.println(mapper.testGet().getText());
        System.out.println(mapper.testGet().getText());
    }

    @Test
    public void name2() {
        Response listInfo = mapper.getListInfo();
        List<Item> data = listInfo.getData();
        System.out.println(data);
    }

    @Test
    public void name3() throws Exception {
        //上传文件测试
        File file = new File(SystemTest.class.getClassLoader().getResource("file.txt").getFile());
        String content = new BufferedReader(new InputStreamReader(new FileInputStream(file))).lines().collect(Collectors.joining("\r\n"));
        Response response = mapper.uploadFile(file);
        String data = response.getData();
        assert content.equals(data);
    }
}
