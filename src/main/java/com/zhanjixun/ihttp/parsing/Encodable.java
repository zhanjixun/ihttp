package com.zhanjixun.ihttp.parsing;

/**
 * 可编码的
 *
 * @author :zhanjixun
 * @date : 2019/11/22 17:44
 * @contact :zhanjixun@qq.com
 */
public interface Encodable {

    void setEncode(boolean encode);

    /**
     * 是否需要编码
     *
     * @return
     */
    boolean encode();


}
