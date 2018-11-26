package com.zhanjixun.ihttp.test.hao6v;

import com.zhanjixun.ihttp.result.html.Result;
import com.zhanjixun.ihttp.result.html.ResultType;
import com.zhanjixun.ihttp.result.html.StringResult;
import lombok.Data;

/**
 * @author :zhanjixun
 * @date : 2018/10/14 10:47
 */
@Data
public class Bean {

    @StringResult(cssSelector = "span", result = @Result(type = ResultType.TEXT))
    private String date;

    @StringResult(cssSelector = "a", result = @Result(type = ResultType.TEXT))
    private String name;

    @StringResult(cssSelector = "a", result = @Result(type = ResultType.HREF))
    private String url;
    
}
