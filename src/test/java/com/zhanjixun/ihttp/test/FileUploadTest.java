package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.POST;
import com.zhanjixun.ihttp.annotations.RequestPart;
import com.zhanjixun.ihttp.annotations.URL;
import com.zhanjixun.ihttp.annotations.UserAgent;
import com.zhanjixun.ihttp.domain.MultipartFile;
import org.junit.Test;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * 上传文件单元测试
 *
 * @author zhanjixun
 * @date 2021-04-14 14:40:39
 */
public class FileUploadTest extends BaseTest {

    @URL("http://localhost:8088")
    @UserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
    public interface FileUploadMapper {

        @POST
        @URL("/postFile")
        Response updloadFile1(@RequestPart(name = "file1") String file);

        @POST
        @URL("/postFile")
        Response updloadFile2(@RequestPart(name = "file1") File file);

        @POST
        @URL("/postFile")
        Response updloadFile3(@RequestPart(name = "file1") MultipartFile file);
    }

    private final FileUploadMapper mapper = IHTTP.getMapper(FileUploadMapper.class);

    @Test
    public void name() {
        File outFile = new File(FileSystemView.getFileSystemView().getHomeDirectory(), "工作记录.md");
        Response response = mapper.updloadFile1(outFile.getAbsolutePath());
    }
}
