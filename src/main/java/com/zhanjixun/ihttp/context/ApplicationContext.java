package com.zhanjixun.ihttp.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 简易的AOP容器
 *
 * @author :zhanjixun
 * @date : 2019/11/22 19:21
 */
public class ApplicationContext {

    private final Map<Class<?>, Object> beanMap = new HashMap<>();

    private static ApplicationContext instance;

    private ApplicationContext() {
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            synchronized (ApplicationContext.class) {
                if (instance == null) {
                    instance = new ApplicationContext();
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        return (T) beanMap.get(type);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanOrCreate(Class<T> type) {
        Object o = beanMap.get(type);
        if (o == null) {
            try {
                registerBean(type.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return (T) beanMap.get(type);
    }

    public void registerBean(Object bean) {
        beanMap.put(bean.getClass(), bean);
    }

}
