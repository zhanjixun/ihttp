package com.zhanjixun.ihttp;

import com.zhanjixun.ihttp.binding.MapperProxyFactory;

/**
 * 系统入口类
 *
 * @author zhanjixun
 */
public class IHTTP {

    public static <T> T getMapper(Class<T> mapperInterface) {
        try {
            MapperProxyFactory<T> mapperProxyFactory = new MapperProxyFactory<>(mapperInterface);
            return mapperProxyFactory.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
        }
    }

}
