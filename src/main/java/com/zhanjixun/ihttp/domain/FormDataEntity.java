package com.zhanjixun.ihttp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okio.Okio;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;

/**
 * 附带文件上传
 *
 * @author :zhanjixun
 * @date : 2018/10/11 20:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDataEntity {

    private String contentType;

    private String fileName;

    private byte[] data;

    public static FormDataEntity create(File file) {
        try {
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            byte[] bytes = Okio.buffer(Okio.source(file)).readByteArray();
            return new FormDataEntity(mimeType, file.getName(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
