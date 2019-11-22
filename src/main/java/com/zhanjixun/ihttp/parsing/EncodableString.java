package com.zhanjixun.ihttp.parsing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 可编码的字符串类型
 *
 * @author :zhanjixun
 * @date : 2019/11/22 17:48
 * @contact :zhanjixun@qq.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncodableString implements Encodable {

    private String name;

    private boolean encode;

    @Override
    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    @Override
    public boolean encode() {
        return encode;
    }

}
