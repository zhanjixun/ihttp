package com.zhanjixun.ihttp.test;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.test.base.NettyServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SystemTest {

    private final TestMapper mapper = IHTTP.getMapper(TestMapper.class);

    private final NettyServer nettyServer = new NettyServer(8088);

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
        Response response = mapper.testGet("v3", 4,
                ImmutableMap.<String, Object>builder().put("m1", "m1Value").put("m2", 2).put("m3", 2.0).build(),
                new TestMapper.Student("中文测试", 23, "JK1132"));
        assert response.getStatus() == 200;
        System.out.println(response.getText());
    }

    @Test
    public void name2() {
        Response response = mapper.testPost(JSON.toJSONString(ImmutableMap.<String, Object>builder().put("m1", "m1Value").put("m2", 2).put("m3", 2.0).build()));
        assert response.getStatus() == 200;
    }

    @Test
    public void name3() {
        Response response = mapper.testJsonHandler();
        assert response.getStatus() == 200;
        TestMapper.Student student = response.getData();
        assert student != null;
        System.out.println(student);

        Response response2 = mapper.testJsonHandler2();
        assert response2.getStatus() == 200;
        TestMapper.Student student2 = response2.getData();
        assert student2 != null;
        System.out.println(student2);

        Response response3 = mapper.testHtmlHandler();
        String response3Data = response3.getData();
        System.out.println(response3Data);

        Response response4 = mapper.testHtmlHandler2();
        Integer response4Data = response4.getData();
        System.out.println(response4Data);
    }
}
