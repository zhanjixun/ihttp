package com.zhanjixun.ihttp.binding;

import com.alibaba.fastjson.JSON;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.RetryPolicy;
import com.zhanjixun.ihttp.annotations.FilePart;
import com.zhanjixun.ihttp.context.ApplicationContext;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.Header;
import com.zhanjixun.ihttp.domain.Param;
import com.zhanjixun.ihttp.parsing.*;
import com.zhanjixun.ihttp.utils.StrUtils;
import com.zhanjixun.ihttp.utils.Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 对应一个Mapper中定义的方法
 *
 * @author zhanjixun
 */
@Data
@Slf4j
public class MapperMethod {

    private Mapper mapper;

    private String name;

    private String url;

    private String requestCharset;

    private String responseCharset;

    private Boolean followRedirects;

    //GET,POST,PUT,DELETE
    private String requestMethod;

    private List<Header> requestHeaders;

    private List<Param> requestParams;

    private List<FileParts> requestMultiParts;

    private int[] assertStatusCode;

    private Boolean disableCookie;

    private List<RandomGenerator> randomGeneratorParams;

    private List<RandomGenerator> randomGeneratorPlaceholders;

    private List<TimestampGenerator> timestampGeneratorParams;

    private List<TimestampGenerator> timestampGeneratorPlaceholders;

    private String requestBody;

    private Retryable retryable;

    private MapperParameter[] parameters;

    public MapperMethod(String name) {
        this.name = name;
    }

    public Response execute(Object... args) throws Exception {
        Request request = buildRequest(args);
        return executeRetryable(request);
    }

    private Request buildRequest(Object... args) {
        Request request = new Request();
        request.setName(name);
        //1.绑定固定内容
        request.setUrl(buildUrl(mapper.getUrl(), getUrl()));
        request.setMethod(getRequestMethod());

        request.setCharset(getRequestCharset());
        request.setResponseCharset(getResponseCharset());

        request.setFollowRedirects(getFollowRedirects());
        request.setBody(getRequestBody());

        request.setHeaders(new ArrayList<>());
        if (mapper.getRequestHeaders() != null) {
            request.getHeaders().addAll(mapper.getRequestHeaders());
        }
        if (getRequestHeaders() != null) {
            request.getHeaders().addAll(getRequestHeaders());
        }

        request.setParams(new ArrayList<>());
        if (mapper.getRequestParams() != null) {
            request.getParams().addAll(mapper.getRequestParams());
        }
        if (getRequestParams() != null) {
            request.getParams().addAll(getRequestParams());
        }

        //2.生成实时内容
        if (mapper.getRandomGeneratorParams() != null) {
            for (RandomGenerator generatorParam : mapper.getRandomGeneratorParams()) {
                String value = generatorRandomValue(generatorParam, request.getCharset());
                request.getParams().add(new Param(generatorParam.getName(), value));
            }
        }
        if (getRandomGeneratorParams() != null) {
            for (RandomGenerator generatorParam : getRandomGeneratorParams()) {
                String value = generatorRandomValue(generatorParam, request.getCharset());
                request.getParams().add(new Param(generatorParam.getName(), value));
            }
        }

        if (mapper.getTimestampGeneratorParams() != null) {
            for (TimestampGenerator generator : mapper.getTimestampGeneratorParams()) {
                String value = generatorTimestampValue(generator);
                request.getParams().add(new Param(generator.getName(), value));
            }
        }
        if (getTimestampGeneratorParams() != null) {
            for (TimestampGenerator generator : getTimestampGeneratorParams()) {
                String value = generatorTimestampValue(generator);
                request.getParams().add(new Param(generator.getName(), value));
            }
        }

        if (mapper.getRandomGeneratorPlaceholders() != null) {
            for (RandomGenerator generator : mapper.getRandomGeneratorPlaceholders()) {
                replacePlaceholder(request, generator.getName(), generatorRandomValue(generator, request.getCharset()));
            }
        }
        if (getRandomGeneratorPlaceholders() != null) {
            for (RandomGenerator generator : getRandomGeneratorPlaceholders()) {
                String replacement = generatorRandomValue(generator, request.getCharset());
                replacePlaceholder(request, generator.getName(), replacement);
            }
        }

        if (mapper.getTimestampGeneratorPlaceholders() != null) {
            for (TimestampGenerator generator : mapper.getTimestampGeneratorPlaceholders()) {
                String replacement = generatorTimestampValue(generator);
                replacePlaceholder(request, generator.getName(), replacement);
            }
        }
        if (getTimestampGeneratorPlaceholders() != null) {
            for (TimestampGenerator generator : getTimestampGeneratorPlaceholders()) {
                String replacement = generatorTimestampValue(generator);
                replacePlaceholder(request, generator.getName(), replacement);
            }
        }

        //3.绑定运行参数
        for (MapperParameter mapperParameter : parameters) {
            Object arg = args[mapperParameter.getIndex()];
            if (mapperParameter.isURLAnnotated()) {
                request.setUrl(buildUrl(request.getUrl(), (String) arg));
            }
            if (Util.isNotEmpty(mapperParameter.getRequestParamNames())) {
                for (EncodableString requestParamName : mapperParameter.getRequestParamNames()) {
                    Class<?> parameterType = mapperParameter.getParameterType();
                    if (parameterType == String.class) {
                        String value = requestParamName.encode() ? StrUtils.URLEncoder((String) arg, request.getCharset()) : (String) arg;
                        request.getParams().add(new Param(requestParamName.getName(), value));
                    } else if (parameterType == Map.class) {
                        Map<String, Object> map = (Map) arg;
                        String suffix = Util.isNotEmpty(requestParamName.getName()) ? requestParamName.getName() + "." : "";
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            request.getParams().add(new Param(suffix + entry.getKey(), String.valueOf(entry.getValue())));
                        }
                    } else {
                        //todo 内省
                    }
                }
            }
            if (Util.isNotEmpty(mapperParameter.getRequestHeaderNames())) {
                for (String headerName : mapperParameter.getRequestHeaderNames()) {
                    request.getHeaders().add(new Header(headerName, (String) arg));
                }
            }
            if (Util.isNotEmpty(mapperParameter.getRequestMultiPartNames())) {
                for (String multiPartName : mapperParameter.getRequestMultiPartNames()) {
                    if (arg instanceof String) {
                        request.getFileParts().add(new FileParts(multiPartName, new File((String) arg)));
                    } else if (arg instanceof File) {
                        request.getFileParts().add(new FileParts(multiPartName, (File) arg));
                    } else {
                        throw new IllegalArgumentException("在方法的参数中使用" + FilePart.class.getName() + "时，被注解的参数类型必须为java.lang.String或者java.io.File");
                    }
                }
            }
            if (mapperParameter.getRequestBody() != null) {
                EncodableObject requestBody = mapperParameter.getRequestBody();
                if (arg instanceof String) {
                    request.setBody(requestBody.encode() ? StrUtils.URLEncoder((String) arg, request.getCharset()) : (String) arg);
                } else {
                    String jsonBody = JSON.toJSONString(arg);
                    request.setBody(requestBody.encode() ? StrUtils.URLEncoder(jsonBody, request.getCharset()) : jsonBody);
                }
            }
        }

        return request;
    }

    private void replacePlaceholder(Request request, String placeholder, String replacement) {
        placeholder = "#{" + placeholder + "}";
        //替换URL
        if (Util.isNotEmpty(request.getUrl())) {
            request.setUrl(request.getUrl().replace(placeholder, replacement));
        }
        //替换请求体
        if (Util.isNotEmpty(request.getBody())) {
            request.setBody(request.getBody().replace(placeholder, replacement));
        }
        //替换请求头
        for (Header header : request.getHeaders()) {
            if (Util.isNotEmpty(header.getValue())) {
                header.setValue(header.getValue().replace(placeholder, replacement));
            }
        }
        //替换请求参数
        for (Param param : request.getParams()) {
            if (Util.isNotEmpty(param.getValue())) {
                param.setValue(param.getValue().replace(placeholder, replacement));
            }
        }
    }

    private String generatorRandomValue(RandomGenerator randomGenerator, String charset) {
        String rawValue = Util.randomString(randomGenerator.getLength(), randomGenerator.getChars());
        return randomGenerator.isEncode() ? StrUtils.URLEncoder(rawValue, charset) : rawValue;
    }

    private String generatorTimestampValue(TimestampGenerator timestampGenerator) {
        return timestampGenerator.getUnit().convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) + "";
    }

    private String buildUrl(String a, String b) {
        a = Util.isEmpty(a) ? "" : a;
        b = Util.isEmpty(b) ? "" : b;
        return b.startsWith("http") ? b : a + b;
    }

    private Response executeRetryable(Request request) throws Exception {
        if (retryable == null && mapper.getRetryable() == null) {
            return mapper.getExecutor().execute(request);
        }
        Retryable retry = retryable == null ? mapper.getRetryable() : retryable;
        Response response = null;
        Exception exception = null;

        Class<? extends Throwable>[] throwable = retry.getThrowable();
        Class<? extends RetryPolicy>[] policy = retry.getPolicy();

        long delay = retry.getDelay();
        long multiplier = retry.getMultiplier();

        for (int i = 0; i < retry.getMaxAttempts(); i++) {
            try {
                response = mapper.getExecutor().execute(request);
            } catch (Exception e) {
                exception = e;
            }
            long delayMillis = multiplier > 0 ? (delay * (int) Math.pow(multiplier, i)) : (delay > 0 ? delay : 0);
            //发生异常情况
            if (exception != null) {
                Exception finalException = exception;
                if (throwable != null && Arrays.stream(throwable).anyMatch(t -> t.isAssignableFrom(finalException.getClass()))) {
                    log.debug("发送http请求发生异常,正在准备第" + (i + 1) + "次重试,延迟" + delayMillis + "毫秒...");
                    if (delayMillis > 0) {
                        Thread.sleep(delayMillis);
                    }
                    continue;
                }
                throw new RuntimeException(exception);
            }

            //触发策略情况
            if (policy != null) {
                Response finalResponse = response;
                if (Arrays.stream(policy).anyMatch(p -> ApplicationContext.getInstance().getBeanOrCreate(p).needRetry(finalResponse))) {
                    log.debug("发送http请求触发策略重试,正在准备第" + (i + 1) + "次重试,延迟" + delayMillis + "毫秒...");
                    if (delayMillis > 0) {
                        Thread.sleep(delayMillis);
                    }
                    continue;
                }
            }
            break;
        }
        return response;
    }

}
