package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.*;

import java.io.File;


@URL("http://localhost:8088")
@UserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
public interface TestMapper {

	@GET
	@URL("/index")
	Response index();

	@POST
	@URL("/upload")
	Response upload(@RequestPart(name = "file") File file);

}