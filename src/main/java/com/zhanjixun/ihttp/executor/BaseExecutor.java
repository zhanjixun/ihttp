package com.zhanjixun.ihttp.executor;


import com.zhanjixun.ihttp.CookiesManager;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import org.springframework.beans.BeanUtils;

/**
 * @author zhanjixun
 */
public abstract class BaseExecutor implements CookiesManager {

    public abstract Response execute(Request request);

    protected <T> T copyProperties(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

}
