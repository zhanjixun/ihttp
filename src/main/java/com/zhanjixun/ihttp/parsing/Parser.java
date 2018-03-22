package com.zhanjixun.ihttp.parsing;

import com.zhanjixun.ihttp.binding.Mapper;

/**
 * @author zhanjixun
 */
public interface Parser {
    /**
     * 解析配置
     *
     * @return Mapper
     */
    Mapper parse();
}
