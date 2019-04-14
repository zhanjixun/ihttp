package com.zhanjixun.ihttp.domain;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.cookie.CookiesStoreImpl;
import com.zhanjixun.ihttp.executor.BaseExecutor;
import com.zhanjixun.ihttp.executor.ComponentsHttpClientExecutor;
import lombok.Builder;
import lombok.Data;

/**
 * 一些配置类
 *
 * @author :zhanjixun
 * @date : 2018/12/3 15:05
 */
@Data
@Builder
public class Configuration {

    private HttpProxy proxy;

    private boolean cookieEnable = true;

    private String CookiesStoreID = null;

    private CookiesStore cookiesStore;

    private Class<? extends BaseExecutor> executor;

    public static Configuration getDefault() {
        return builder()
                .proxy(null)
                .cookieEnable(true)
                .cookiesStore(new CookiesStoreImpl())
                .executor(ComponentsHttpClientExecutor.class)
                .build();
    }
}
