package com.zhanjixun.ihttp.result.html.annotations;

/**
 * @author :zhanjixun
 * @date : 2018/12/19 13:47
 */
public @interface MapEntry {
    /**
     * css选择器
     *
     * @return
     */
    String cssSelector();

    /**
     * 对应键的css选择器
     *
     * @return
     */
    String keyCssSelector();

    /**
     * 对应值的css选择器
     *
     * @return
     */
    String valueCssSelector();
}
