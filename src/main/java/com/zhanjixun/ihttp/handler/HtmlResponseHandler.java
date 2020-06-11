package com.zhanjixun.ihttp.handler;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.handler.annotations.CSSSelector;
import com.zhanjixun.ihttp.handler.data.SelectType;
import com.zhanjixun.ihttp.utils.ReflectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author zhanjixun
 * @date 2020-06-11 15:02:33
 */
public class HtmlResponseHandler implements ResponseHandler {
    @Override
    public Object handle(Method method, MapperMethod mapperMethod, Response response) {
        Class<?> returnType = mapperMethod.getReturnType();

        CSSSelector cssSelector = method.getAnnotation(CSSSelector.class);
        if (cssSelector != null) {
            Document document = Jsoup.parse(response.getText());
            Elements selectedElements = document.select(cssSelector.selector());
            SelectType selectType = cssSelector.selectType();
            
            //基本类型和String
            if (ReflectUtils.isStringOrPrimitive(returnType)) {
                if (selectedElements.isEmpty()) {
                    return null;
                }
                Element selectedElement = selectedElements.get(0);
                if (selectType == SelectType.NODE_NAME) {
                    return selectedElement.nodeName();
                }
                if (selectType == SelectType.HTML) {
                    return selectedElement.html();
                }
                if (selectType == SelectType.TEXT) {
                    return selectedElement.text();
                }
                if (selectType == SelectType.ATTR) {
                    return selectedElement.attr(cssSelector.attr());
                }
            }

            //如果是集合类型
            if (Collection.class.isAssignableFrom(returnType)) {

            }

            //认为是实体类型
        }
        return null;
    }

}
