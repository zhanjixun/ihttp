package com.zhanjixun.ihttp.handler;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.exception.ResponseHandleException;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author zhanjixun
 * @date 2020-06-11 13:52:43
 */
public class ImageResponseHandler implements ResponseHandler {

    @Override
    public Object handle(Method method, MapperMethod mapperMethod, Response response) {
        try {
            return ImageIO.read(new ByteArrayInputStream(response.getBody()));
        } catch (IOException e) {
            throw new RuntimeException(new ResponseHandleException(e));
        }
    }

}
