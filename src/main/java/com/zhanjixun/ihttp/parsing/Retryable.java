package com.zhanjixun.ihttp.parsing;

import com.zhanjixun.ihttp.RetryPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :zhanjixun
 * @date : 2019/11/22 11:45
 * @contact :zhanjixun@qq.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Retryable {

    private Class<? extends Throwable>[] throwable;

    private Class<? extends RetryPolicy>[] policy;

    private int maxAttempts;

    private long delay;

    private long multiplier;

}
