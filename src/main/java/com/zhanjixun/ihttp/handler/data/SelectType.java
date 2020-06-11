package com.zhanjixun.ihttp.handler.data;

/**
 * 代表一个节点的某个部分
 *
 * @author zhanjixun
 * @date 2020-06-11 17:41:45
 */
public enum SelectType {

    /**
     * 节点名称
     */
    NODE_NAME,
    /**
     * 节点的html文本
     */
    HTML,
    /**
     * 节点文本
     */
    TEXT,
    /**
     * 节点属性
     * 需要在 CSSSelector.attr中指定属性名称
     */
    ATTR
}
