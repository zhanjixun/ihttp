package com.zhanjixun.ihttp.result.html;

/**
 * 选取html元素的数据类型
 *
 * @author :zhanjixun
 * @date : 2018/8/20 14:33
 */
public enum ResultType {
    /**
     * 默认值
     */
//    NONE,
    /**
     * 获取元素中Text内容
     */
    TEXT,
    /**
     * 获取元素中属性内容
     * 需要在com.zhanjixun.ihttp.result.Result中value指定属性的key
     */
    ATTRIBUTE,
    /**
     * 获取元素整个html标签
     */
    HTML,
    /**
     * 获取a标签中超链接内容
     */
    HREF,
    /**
     * 获取元素中的value值
     */
    VALUE

}
