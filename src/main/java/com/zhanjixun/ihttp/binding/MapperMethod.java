package com.zhanjixun.ihttp.binding;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.RequestPart;
import com.zhanjixun.ihttp.domain.FormData;
import com.zhanjixun.ihttp.domain.FormDatas;
import com.zhanjixun.ihttp.domain.Header;
import com.zhanjixun.ihttp.domain.Param;
import com.zhanjixun.ihttp.handler.ResponseHandler;
import com.zhanjixun.ihttp.parsing.EncodableObject;
import com.zhanjixun.ihttp.parsing.EncodableString;
import com.zhanjixun.ihttp.parsing.RandomGenerator;
import com.zhanjixun.ihttp.parsing.TimestampGenerator;
import com.zhanjixun.ihttp.utils.ReflectUtils;
import com.zhanjixun.ihttp.utils.StrUtils;
import com.zhanjixun.ihttp.utils.Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
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

    private Class<?> returnType;

    private String url;

    private String requestCharset;

    private String responseCharset;

    private Boolean followRedirects;

    private String requestMethod;

    private List<Header> requestHeaders;

    private List<Param> requestParams;

    private List<FormDatas> requestMultiParts;

    private Boolean disableCookie;

    private List<RandomGenerator> randomGeneratorParams;

    private List<RandomGenerator> randomGeneratorPlaceholders;

    private List<TimestampGenerator> timestampGeneratorParams;

    private List<TimestampGenerator> timestampGeneratorPlaceholders;

    private String requestBody;

    private MapperParameter[] parameters;

    private ResponseHandler responseHandler = new ResponseHandler();

    public MapperMethod(String name) {
        this.name = name;
    }

    public Response execute(Object... args) throws Exception {
        Request request = buildRequest(args);
        return mapper.getExecutor().execute(request);
    }

    private Request buildRequest(Object... args) {
        Request request = new Request();
        //1.绑定固定内容
        bingConstValue(request);
        //2.生成实时内容
        bingGenerateValue(request);
        //3.绑定运行参数
        for (MapperParameter mapperParameter : parameters) {
            bingParameterValue(request, mapperParameter, args[mapperParameter.getIndex()]);
        }
        return request;
    }

    //绑定固定内容
    private void bingConstValue(Request request) {
        request.setUrl(buildUrl(mapper.getUrl(), getUrl()));
        request.setMethod(getRequestMethod());

        request.setCharset(getRequestCharset());

        request.setFollowRedirects(Util.defaultIfNull(getFollowRedirects(), false));
        request.setBody(getRequestBody());

        request.setHeaders(new ArrayList<>());
        if (mapper.getRequestHeaders() != null) {
            request.getHeaders().addAll(mapper.getRequestHeaders());
        }
        if (getRequestHeaders() != null) {
            List<Header> requestHeaders = getRequestHeaders();
            for (Header requestHeader : requestHeaders) {
                //同时定义在接口和方法上 只使用方法上
                request.getHeaders().removeIf(h -> requestHeader.getName().equals(h.getName()));
            }
            request.getHeaders().addAll(requestHeaders);
        }

        request.setParams(new ArrayList<>());
        if (getRequestParams() != null) {
            List<Param> requestParams = getRequestParams();
            for (Param requestParam : requestParams) {
                //同时定义在接口和方法上 只使用方法上
                request.getParams().removeIf(p -> p.getName().equals(requestParam.getName()));
            }
            request.getParams().addAll(requestParams);
        }

        request.setFileParts(new ArrayList<>());
        if (getRequestMultiParts() != null) {
            for (FormDatas requestMultiPart : getRequestMultiParts()) {
                request.getFileParts().add(requestMultiPart);
            }
        }
    }

    //绑定生成类的值
    private void bingGenerateValue(Request request) {
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
    }

    //绑定运行参数
    private void bingParameterValue(Request request, MapperParameter mapperParameter, Object arg) {
        Class<?> parameterType = mapperParameter.getParameterType();
        if (mapperParameter.isURLAnnotated()) {
            request.setUrl(buildUrl(request.getUrl(), (String) arg));
        }

        if (Util.isNotEmpty(mapperParameter.getRequestParamNames())) {
            for (EncodableString requestParamName : mapperParameter.getRequestParamNames()) {
                //基本类型及其封装类
                if (ReflectUtils.isStringOrPrimitive(parameterType)) {
                    String value = requestParamName.encode() ? StrUtils.URLEncoder(arg.toString(), request.getCharset()) : arg.toString();
                    request.getParams().add(new Param(requestParamName.getName(), value));
                    continue;
                }
                //支持序列化的对象 通常是Map<String,Object>或者实体类
                String suffix = Util.isNotEmpty(requestParamName.getName()) ? requestParamName.getName() + "." : "";
                JSONObject jsonObject = (JSONObject) JSON.toJSON(arg);
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    request.getParams().add(new Param(suffix + entry.getKey(), String.valueOf(entry.getValue())));
                }
            }
        }

        if (Util.isNotEmpty(mapperParameter.getRequestHeaderNames())) {
            for (String headerName : mapperParameter.getRequestHeaderNames()) {
                //同时定义在方法上和参数上 只使用参数的
                request.getHeaders().removeIf(h -> h.getName().equals(headerName));
                request.getHeaders().add(new Header(headerName, (String) arg));
            }
        }

        if (Util.isNotEmpty(mapperParameter.getRequestMultiPartNames())) {
            for (String multiPartName : mapperParameter.getRequestMultiPartNames()) {
                if (arg instanceof String) {
                    request.getFileParts().add(new FormDatas(multiPartName, FormData.create(new File((String) arg))));
                    continue;
                }
                if (arg instanceof File) {
                    request.getFileParts().add(new FormDatas(multiPartName, FormData.create((File) arg)));
                    continue;
                }
                if (arg instanceof FormData) {
                    request.getFileParts().add(new FormDatas(multiPartName, (FormData) arg));
                    continue;
                }
                throw new IllegalArgumentException(RequestPart.class.getName() + "不支持类型：" + parameterType.getName());
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

        if (mapperParameter.getPlaceholder() != null) {
            EncodableString placeholder = mapperParameter.getPlaceholder();
            String value = placeholder.isEncode() ? StrUtils.URLEncoder(arg.toString(), request.getCharset()) : arg.toString();
            replacePlaceholder(request, placeholder.getName(), value);
        }
    }

    //替换占位符
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

    //生成随机字符串
    private String generatorRandomValue(RandomGenerator randomGenerator, String charset) {
        String rawValue = Util.randomString(randomGenerator.getLength(), randomGenerator.getChars());
        return randomGenerator.isEncode() ? StrUtils.URLEncoder(rawValue, charset) : rawValue;
    }

    //生成时间戳
    private String generatorTimestampValue(TimestampGenerator timestampGenerator) {
        return timestampGenerator.getUnit().convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) + "";
    }

    //拼接URL
    private String buildUrl(String a, String b) {
        a = Util.isEmpty(a) ? "" : a;
        b = Util.isEmpty(b) ? "" : b;
        return b.startsWith("http") ? b : a + b;
    }

    @Override
    public String toString() {
        return "MapperMethod:" + mapper + "#" + name;
    }
}
