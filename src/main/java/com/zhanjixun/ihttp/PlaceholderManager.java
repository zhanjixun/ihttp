package com.zhanjixun.ihttp;

/**
 * 可以管理占位符
 *
 * @author zhanjixun
 * @date 2020-11-18 15:59:43
 */
public interface PlaceholderManager {
    /**
     * 获取占位符的值
     *
     * @param key
     * @return
     */
    Object getValue(String key);

    /**
     * 设置占位符
     *
     * @param key
     * @param value
     */
    void setValue(String key, Object value);

    /**
     * 获取所有占位符的键
     *
     * @return
     */
    String[] getKeys();
}
