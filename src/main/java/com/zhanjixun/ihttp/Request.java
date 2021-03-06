package com.zhanjixun.ihttp;

import com.zhanjixun.ihttp.domain.FormDatas;
import com.zhanjixun.ihttp.domain.Header;
import com.zhanjixun.ihttp.domain.Param;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * http请求实体
 *
 * @author zhanjixun
 */
@Data
public class Request implements Serializable, Cloneable {

    private static final long serialVersionUID = 3720684088323984812L;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 请求编码
     */
    private String method;
    /**
     * 跟随重定向
     */
    private Boolean followRedirects;
    /**
     * 请求体
     */
    private String body;
    /**
     * 请求字符编码
     */
    private String charset;
    /**
     * 请求头
     */
    private List<Header> headers;
    /**
     * 请求参数
     */
    private List<Param> params;
    /**
     * 文件
     */
    private List<FormDatas> fileParts;

    @Override
    public String toString() {
        return getMethod() + " " + getUrl();
    }

    @Override
    public Request clone() {
        Request target = new Request();
        BeanUtils.copyProperties(this, target);
        return target;
    }
}
