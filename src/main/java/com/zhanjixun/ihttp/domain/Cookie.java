package com.zhanjixun.ihttp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author :zhanjixun
 * @date : 2018/9/28 10:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cookie {

    private String name;
    private String value;

    private String domain;
    private String path;

    private Date expiryDate;

    private String comment;

    private boolean isSecure;
    private int version = 0;

    private boolean httpOnly;
}
