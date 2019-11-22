package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.parsing.RandomGenerator;
import com.zhanjixun.ihttp.parsing.RetryableFunction;
import com.zhanjixun.ihttp.parsing.TimestampGenerator;
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

    private String url;

    private List<Map<String, String>> requestParams;

    private List<Map<String, String>> requestHeaders;

    private List<Map<String, String>> requestMultiParts;

    private int[] assertStatusCode;

    private Boolean disableCookie;

    private List<RandomGenerator> randomParams;

    private List<RandomGenerator> randomPlaceholders;

    private List<TimestampGenerator> timestampParams;

    private List<TimestampGenerator> timestampPlaceholders;

    private String requestBody;

    private RetryableFunction retryable;

    private MapperParameter[] parameters;

    public MapperMethod(Mapper declaringMapper, String name) {
        this.declaringMapper = declaringMapper;
        this.name = name;
    }

    public Response execute(Object... args) throws Exception {

        return null;
    }

    private Request buildRequest(Object... args) {

        return null;
    }

}
