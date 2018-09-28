package com.zhanjixun.ihttp;

import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.POST;
import com.zhanjixun.ihttp.binding.MapperProxyFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * 系统入口类
 *
 * @author zhanjixun
 */
public class IHTTP {

    @SuppressWarnings("unchecked")
    public static <T> T getMapper(Class<T> mapperInterface) {
        try {
            MapperProxyFactory<T> mapperProxyFactory = new MapperProxyFactory(mapperInterface);
            return mapperProxyFactory.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    public static <T> boolean isMapper(Class<T> mapperClass) {
        return Arrays.stream(mapperClass.getDeclaredMethods()).parallel().anyMatch(m -> Objects.nonNull(m.getAnnotation(GET.class)) || Objects.nonNull(m.getAnnotation(POST.class)));
    }

}
