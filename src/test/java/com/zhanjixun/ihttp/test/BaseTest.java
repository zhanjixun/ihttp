package com.zhanjixun.ihttp.test;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BaseTest {

    private final StopWatch stopWatch = new StopWatch();

    @Before
    public void startServer() {
        stopWatch.start();
    }

    @After
    public void shutdownServer() {
        stopWatch.stop();
        long duration = stopWatch.getTotalTimeMillis() - TimeZone.getDefault().getRawOffset();
        String format = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(duration));
        log.info("程序用时：" + format);
    }

}
