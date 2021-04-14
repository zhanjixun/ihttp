package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.test.base.NettyServer;
import org.junit.After;
import org.junit.Before;
import org.springframework.util.StopWatch;

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
        System.out.println("程序用时：" + stopWatch.prettyPrint());
    }

}
