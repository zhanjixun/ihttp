package com.zhanjixun.ihttp.domain;

import lombok.Data;

/**
 * 一个running time文件，可以使用来分批次上传文件
 *
 * @author :zhanjixun
 * @date : 2018/11/29 19:12
 */
@Data
public class ByteArrayFile {

    private byte[] data;

    private String fileName;

    private long lastModified;

    private String mineType;
}
