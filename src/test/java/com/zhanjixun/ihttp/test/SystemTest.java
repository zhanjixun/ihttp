package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SystemTest extends BaseTest {

    private final TestMapper mapper = IHTTP.getMapper(TestMapper.class);

    @URL("#{url}")
    @RequestHeader(name = "headerName", value = "headerValue")
    public interface TestMapper {

        default String testDefaultMethod() {
            return "testDefaultMethod";
        }

        @GET
        @URL("/testGet")
        @RequestHeader(name = "headerName", value = "headerValue")
        @RequestHeader(name = "headerName1", value = "headerValue1")
        Response testGet(@Param("url") String url);

        @GET(followRedirects = false)
        @URL("/followRedirects")
        Response followRedirects(@Param("url") String url);

        @POST
        @URL("/testPost")
        Response testPost(@Param("url") String url);

        @PUT
        @URL("/testPut")
        Response testPut(@Param("url") String url);

        @DELETE
        @URL("/testDelete")
        Response testDelete(@Param("url") String url);
    }

    @Test
    public void name1() {
        assertEquals(mapper.testDefaultMethod(), "testDefaultMethod");
    }

    @Test
    public void name2() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();
        String url = mockWebServer.url("/").toString();

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("testGet"));
        Response response = mapper.testGet(url);
        assertEquals(response.getText(), "testGet");

        RecordedRequest recordedRequest1 = mockWebServer.takeRequest();
        assertEquals(recordedRequest1.getMethod(), "GET");
        assertEquals(recordedRequest1.getPath(), "/testGet");

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("testPost"));
        assertEquals(mapper.testPost(url).getText(), "testPost");
        RecordedRequest recordedRequest2 = mockWebServer.takeRequest();
        assertEquals(recordedRequest2.getMethod(), "POST");

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("testPut"));
        assertEquals(mapper.testPut(url).getText(), "testPut");
        RecordedRequest recordedRequest3 = mockWebServer.takeRequest();
        assertEquals(recordedRequest3.getMethod(), "PUT");

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("testDelete"));
        assertEquals(mapper.testDelete(url).getText(), "testDelete");
        RecordedRequest recordedRequest4 = mockWebServer.takeRequest();
        assertEquals(recordedRequest4.getMethod(), "PUT");

        //测试get请求重定向
        mockWebServer.enqueue(new MockResponse().setResponseCode(302).addHeader("Location", url + "/redirect"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("ok"));
        Response response1 = mapper.testGet(url);
        assertEquals(response1.getStatus(), 200);
        assertEquals(response1.getText(), "ok");

        mockWebServer.enqueue(new MockResponse().setResponseCode(302).addHeader("Location", url + "/redirect"));
        Response response2 = mapper.followRedirects(url);
        assertEquals(response2.getStatus(), 302);
        assertEquals(response2.getLocation(), url + "/redirect");
    }

    @Test
    public void name3() throws InterruptedException {
        MockWebServer mockWebServer = new MockWebServer();
        String url = mockWebServer.url("/").toString();

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("testGet"));
        Response response = mapper.testGet(url);
        assertEquals(response.getText(), "testGet");
        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertEquals(recordedRequest.getHeader("headerName"), "headerValue");
        assertEquals(recordedRequest.getHeader("headerName1"), "headerValue1");
    }
}
