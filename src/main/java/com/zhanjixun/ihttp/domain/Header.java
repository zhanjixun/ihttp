package com.zhanjixun.ihttp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 头部信息
 *
 * @author :zhanjixun
 * @date : 2018/11/28 23:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Header {

	private String name;

	private String value;
}
