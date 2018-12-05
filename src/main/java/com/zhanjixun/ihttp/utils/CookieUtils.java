package com.zhanjixun.ihttp.utils;

import org.springframework.beans.BeanUtils;

/**
 * @author :zhanjixun
 * @date : 2018/12/5 10:31
 */
public class CookieUtils {

    public static <T> T copyProperties(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }


}
