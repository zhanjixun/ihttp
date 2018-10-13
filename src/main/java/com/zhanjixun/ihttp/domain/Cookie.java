package com.zhanjixun.ihttp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author :zhanjixun
 * @date : 2018/9/28 10:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cookie {

    private String comment;

    private String domain;

    private Date expiryDate;

    private String path;

    private boolean isSecure;

    private boolean hasPathAttribute = false;

    private boolean hasDomainAttribute = false;

    private int version = 0;

}
