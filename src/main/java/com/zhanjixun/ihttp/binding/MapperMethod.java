package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.parsing.Random;
import com.zhanjixun.ihttp.parsing.Retryable;
import com.zhanjixun.ihttp.parsing.Timestamp;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 对应一个Mapper中定义的方法
 *
 * @author zhanjixun
 */
@Data
public class MapperMethod {

    private Mapper declaringMapper;

    private String name;

    //用于缓存Request,避免重复组装一些固定的属性
    private Request cacheRequest;

    //注解属性

    private String url;

    private String requestCharset;

    private String responseCharset;

    private Boolean followRedirects;

    //GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
    private String requestMethod;

    private Map<String, String> requestParams;

    private Map<String, String> requestHeaders;

    private Map<String, String> requestMultiParts;

    private int[] assertStatusCode;

    private Boolean disableCookie;

    private List<Random> randomParams;

    private List<Random> randomPlaceholders;

    private List<Timestamp> timestampParams;

    private List<Timestamp> timestampPlaceholders;

    private String requestBody;

    private Retryable retryable;

    private MapperParameter[] parameters;

    public MapperMethod(Mapper declaringMapper, String name) {
        this.declaringMapper = declaringMapper;
        this.name = name;
    }

    public Response execute(Object... args) throws Exception {

        return null;
    }

    private Request buildRequest(Object... args) {
        if (cacheRequest == null) {

        }

        //生成实时内容

        //替换占位符

        //绑定运行参数


        return null;
    }

}
