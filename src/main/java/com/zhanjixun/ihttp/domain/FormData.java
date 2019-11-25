package com.zhanjixun.ihttp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 附带文件上传
 *
 * @author :zhanjixun
 * @date : 2018/10/11 20:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormData {

	private String contentType;

	private String fileName;

	private byte[] data;

}
