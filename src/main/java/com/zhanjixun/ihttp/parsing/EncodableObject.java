package com.zhanjixun.ihttp.parsing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :zhanjixun
 * @date : 2019/11/22 19:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncodableObject implements Encodable {

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
