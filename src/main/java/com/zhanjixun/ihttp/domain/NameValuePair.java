package com.zhanjixun.ihttp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 键值对
 *
 * @author :zhanjixun
 * @date : 2018/10/13 23:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameValuePair {

    private String name;

    private String value;

}
