package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.test.base.NettyServer;
import org.junit.After;
import org.junit.Before;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author zhanjixun
 * @date 2021-04-14 14:41:31
 */
public class BaseTest {

    private final StopWatch stopWatch = new StopWatch();

    private final NettyServer nettyServer = new NettyServer(8088);

    @Before
    public void startServer() {
        stopWatch.start();
        nettyServer.start();
    }

    @After
    public void shutdownServer() {
        nettyServer.shutdownGracefully();
        stopWatch.stop();
        long duration = stopWatch.getTotalTimeMillis() - TimeZone.getDefault().getRawOffset();
        String format = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(duration));
        System.out.println("程序用时：" + format);
    }

}
