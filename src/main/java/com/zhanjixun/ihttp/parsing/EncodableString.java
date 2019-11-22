package com.zhanjixun.ihttp.parsing;

import lombok.Data;

/**
 * 可编码的字符串类型
 *
 * @author :zhanjixun
 * @date : 2019/11/22 17:48
 * @contact :zhanjixun@qq.com
 */
@Data
public class EncodableString implements Encodable {

    private String name;

    @Override
    public boolean encode() {
        return false;
    }

}
