package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.parsing.EncodableObject;
import com.zhanjixun.ihttp.parsing.EncodableString;
import lombok.Data;

import java.util.List;

/**
 * 映射形式参数
 *
 * @author :zhanjixun
 * @date : 2019/11/22 11:54
 * @contact :zhanjixun@qq.com
 */
@Data
public class MapperParameter {

    private final String name;

    private final int index;

    //注解属性

    private boolean isURLAnnotated;

    private List<EncodableString> requestParamNames;

    private List<String> requestHeaderNames;

    private List<String> requestMultiPartNames;

    private EncodableObject requestBody;

    private EncodableString placeholder;

    private EncodableObject paramMap;

    private EncodableObject paramObject;

}
