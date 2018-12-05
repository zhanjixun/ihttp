package com.zhanjixun.ihttp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :zhanjixun
 * @date : 2018/12/3 15:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpProxy {

    private String hostName;

    private int port;

}
