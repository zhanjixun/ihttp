package com.zhanjixun.ihttp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okio.Okio;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 附带文件上传
 *
 * @author :zhanjixun
 * @date : 2018/10/11 20:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipartFile {

    private String contentType;

    private String fileName;

    private byte[] data;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    public static MultipartFile create(File file) {
        try {
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            byte[] bytes = Okio.buffer(Okio.source(file)).readByteArray();
            return new MultipartFile(mimeType, file.getName(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 分片上传文件
     *
     * @param file   文件
     * @param from   分片开始
     * @param length 长度
     * @return
     */
    public static MultipartFile create(File file, int from, int length) {
        try {
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            byte[] bytes = Okio.buffer(Okio.source(file)).readByteArray();
            byte[] chunkData = Arrays.copyOfRange(bytes, from, from + length);
            return new MultipartFile(mimeType, file.getName(), chunkData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
