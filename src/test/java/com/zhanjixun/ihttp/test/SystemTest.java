package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.Placeholder;
import com.zhanjixun.ihttp.annotations.URL;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SystemTest extends BaseTest {

    private final TestMapper mapper = IHTTP.getMapper(TestMapper.class);

    @URL("#{url}")
    public interface TestMapper {
        @GET
        @URL("/testGet")
        Response testGet(@Placeholder("url") String url);

        @GET(followRedirects = false)
        @URL("/followRedirects")
        Response followRedirects(@Placeholder("url") String url);
        
        default String testDefaultMethod() {
            return "TestMapper#testDefaultMethod";
        }
    }

    @Test
    public void test() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("ok"));
        String url = mockWebServer.url("/").toString();
        Response response = mapper.testGet(url);
        assert response.getText().equals("ok");
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assert "GET".equals(recordedRequest.getMethod());
        assert "/testGet".equals(recordedRequest.getPath());

        mockWebServer.enqueue(new MockResponse().setResponseCode(302).addHeader("Location", url + "/redirect"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("ok"));
        assert mapper.testGet(url).getStatus() == 200;

        mockWebServer.enqueue(new MockResponse().setResponseCode(302).addHeader("Location", url + "/redirect"));
        assert mapper.followRedirects(url).getStatus() == 302;
    }

    @Test
    public void name2() {
        assert mapper.testDefaultMethod().equals("TestMapper#testDefaultMethod");
    }
}
