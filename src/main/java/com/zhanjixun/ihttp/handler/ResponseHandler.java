package com.zhanjixun.ihttp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.handler.annotations.CSSSelector;
import com.zhanjixun.ihttp.handler.annotations.JsonPath;
import com.zhanjixun.ihttp.handler.enums.SelectType;
import com.zhanjixun.ihttp.utils.ReflectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.NumberUtils;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author :zhanjixun
 * @date : 2019/11/26 11:05
 * @contact :zhanjixun@qq.com
 */
public class ResponseHandler {

    public Object handle(Method method, MapperMethod mapperMethod, Response response) {
        Class<?> returnType = mapperMethod.getReturnType();
        String contentType = response.getContentType();

        try {
            if (returnType.getName().equals("void")) {
                return null;
            }
            //含有JsonPath注解或者内容为json 将被认为是json解析
            if (method.isAnnotationPresent(JsonPath.class) || (contentType != null && contentType.contains("json"))) {
                response.setHandleSupplier(() -> handleJsonResult(method.getAnnotation(JsonPath.class), response));
                return response;
            }
            //含有CSSSelector注解或者内容为html 将被认为是html解析
            if (method.isAnnotationPresent(CSSSelector.class) || (contentType != null && contentType.contains("html"))) {
                response.setHandleSupplier(() -> handleHtmlResult(method.getAnnotation(CSSSelector.class), response));
                return response;
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object handleHtmlResult(CSSSelector cssSelector, Response response) {
        if (cssSelector == null) {
            return null;
        }
        Class returnType = cssSelector.returnType();
        Document document = Jsoup.parse(response.getText());
        Elements selectedElements = document.select(cssSelector.selector());

        //基本类型和String
        if (ReflectUtils.isStringOrPrimitive(returnType)) {
            if (selectedElements.isEmpty()) {
                return null;
            }
            Element selectedElement = selectedElements.get(0);
            String value = findSelectNode(cssSelector, selectedElement);
            if (value == null || returnType == String.class) {
                return value;
            }
            if (ReflectUtils.isPrimitiveOrItsWrapper(returnType)) {
                return NumberUtils.parseNumber(value, returnType);
            }
        }

        //如果是集合类型
        if (Collection.class.isAssignableFrom(returnType)) {

        }

        //认为是实体类型
        return null;
    }

    private String findSelectNode(CSSSelector cssSelector, Element selectedElement) {
        if (cssSelector.selectType() == SelectType.NODE_NAME) {
            return selectedElement.nodeName();
        }
        if (cssSelector.selectType() == SelectType.HTML) {
            return selectedElement.html();
        }
        if (cssSelector.selectType() == SelectType.TEXT) {
            return selectedElement.text();
        }
        if (cssSelector.selectType() == SelectType.ATTR) {
            return selectedElement.attr(cssSelector.attr());
        }
        return null;
    }

    private Object handleJsonResult(JsonPath jsonPath, Response response) {
        if (jsonPath == null) {
            return null;
        }
        Class<?> returnType = jsonPath.returnType();
        Object obj = JSONPath.read(response.getText(), jsonPath.path());
        //string及基本类型
        if (ReflectUtils.isStringOrPrimitive(returnType)) {
            return obj;
        }
        if (obj instanceof Collection) {
            return JSON.parseArray(JSON.toJSONString(obj), returnType);
        }
        return JSON.parseObject(JSON.toJSONString(obj), returnType);
    }
}
