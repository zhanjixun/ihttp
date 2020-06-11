package com.zhanjixun.ihttp.test;

import com.alibaba.fastjson.JSON;
import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.test.base.NettyServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SysTest {

    private TestMapper mapper = IHTTP.getMapper(TestMapper.class);

    private NettyServer nettyServer = new NettyServer(8088);

    @Before
    public void startServer() {
        nettyServer.start();
    }

    @After
    public void shutdownServer() {
        nettyServer.shutdownGracefully();
    }

    @Test
    public void name() {
        Map<String, Object> map = new HashMap<>();
        map.put("m1", "m1Value");
        map.put("m2", 2);
        map.put("m3", 2.0);
        Student beanParam = new Student("中文测试", 23, "JK1132");
        Response response = mapper.testGet("value3", 4, map, beanParam);
        System.out.println(response.getText());
    }

    @Test
    public void name1() throws Exception {
        URL resource = SysTest.class.getClassLoader().getResource("file.txt");
        mapper.upload(new File(resource.toURI()));
    }

    @Test
    public void name2() {
        Map<String, Object> map = new HashMap<>();
        map.put("m1", "m1Value");
        map.put("m2", 2);
        map.put("m3", 2.0);
        mapper.testPost(JSON.toJSONString(map));
    }

    @Test
    public void name3() {
        Map<String, Object> map = new HashMap<>();
        map.put("m1", "m1Value");
        map.put("m2", 2);
        map.put("m3", 2.0);
        mapper.testPut(map);
    }

    @Test
    public void name4() {
        mapper.testDel(1);
    }


}
