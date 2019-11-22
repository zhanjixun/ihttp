package com.zhanjixun.ihttp.parsing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author :zhanjixun
 * @date : 2019/11/22 11:49
 * @contact :zhanjixun@qq.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timestamp {

    private String name;

    private TimeUnit unit;
}
