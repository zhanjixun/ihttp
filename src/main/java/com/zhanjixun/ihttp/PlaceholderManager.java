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
     * @param name
     * @return
     */
    Object getPlaceholderValue(String name);

    /**
     * 设置占位符
     *
     * @param name
     * @param value
     */
    void setPlaceholderValue(String name, Object value);

    /**
     * 移除占位符
     *
     * @param name
     */
    void removePlaceholder(String name);

    /**
     * 清空占位符
     */
    void clearPlaceholder();

    /**
     * 获取所有占位符的键
     *
     * @return
     */
    String[] getPlaceholderNames();
}
