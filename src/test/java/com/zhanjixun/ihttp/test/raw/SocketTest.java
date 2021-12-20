package com.zhanjixun.ihttp.test.raw;

import com.zhanjixun.ihttp.test.BaseTest;
import okio.Okio;
import org.junit.Test;

import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhanjixun
 * @date 2021-04-16 10:58:23
 */
public class SocketTest extends BaseTest {

    @Test
    public void name() throws Exception {
        TimeUnit.SECONDS.sleep(1);

        String request = Okio.buffer(Okio.source(SocketTest.class.getClassLoader().getResource("raw_request").openStream())).readUtf8();
        System.out.println(request);
        Matcher matcher = Pattern.compile("Host: (.*)\r\n").matcher(request);
        String host = matcher.find() ? matcher.group(1) : "";

        int port = host.contains(":") ? Integer.parseInt(host.split(":")[1]) : 80;
        host = host.contains(":") ? host.split(":")[0] : host;

        Socket client = new Socket(host, port);
        OutputStream outputStream = client.getOutputStream();
        outputStream.write((request).getBytes());

        String response = Okio.buffer(Okio.source(client.getInputStream())).readUtf8();
        System.out.println(response);
    }
    

}
