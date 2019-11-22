package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.domain.HttpProxy;
import com.zhanjixun.ihttp.executor.Executor;
import com.zhanjixun.ihttp.parsing.RandomGenerator;
import com.zhanjixun.ihttp.parsing.RetryableFunction;
import com.zhanjixun.ihttp.parsing.TimestampGenerator;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对应一个定义的Mapper接口
 *
 * @author zhanjixun
 * @date 2018/03/22 22:42
 */
@Data
public class Mapper {

    private final Class<?> mapperInterface;

    private final List<MapperMethod> methods;

    private Executor executor;


    //注解属性

    private Configuration configuration;

    private String url;

    //TODO 此处有bug 不支持重复key
    private List<Map<String, String>> headers;

    private String cookieJar;

    private Boolean disableCookie;

    private Class<? extends Executor> httpExecutor;

    private HttpProxy proxy;

    private List<RandomGenerator> randomParams;

    private List<RandomGenerator> randomPlaceholders;

    private String responseCharset;

    private RetryableFunction retryable;

    private List<TimestampGenerator> timestampParams;

    private List<TimestampGenerator> timestampPlaceholders;

    public Mapper(Class<?> mapperInterface, List<MapperMethod> methods) {
        this.mapperInterface = mapperInterface;
        this.methods = methods;
    }

    public MapperMethod getMapperMethod(String name) {
        return methods.stream().collect(Collectors.toMap(MapperMethod::getName, d -> d)).get(name);
    }

}

