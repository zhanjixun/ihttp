package com.zhanjixun.ihttp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @author :zhanjixun
 * @date : 2018/10/11 20:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiParts {

    private String name;

    private File filePart;

}
