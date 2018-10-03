package com.zhanjixun.ihttp.test.gitee;

import com.zhanjixun.ihttp.CookiesManager;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.*;

import java.util.Map;

@URL("https://gitee.com")
@UserAgent("zhanjixun@qq.com")
public interface Gitee extends CookiesManager {

    @GET()
    @URL("/login")
    Response index();

    @POST
    @URL("/login")
    @UserAgent("httpclient")//这个请求头会覆盖类级别的请求头
    @Param(name = "utf8", value = "✓")
    @Param(name = "redirect_to_url")
    @Param(name = "captcha")
    @Param(name = "user[remember_me]")
    @Param(name = "commit", value = "登 录")
    Response login(@Param(name = "authenticity_token") String token,
//                   @Param(name = "user[login]") String email,
//                   @Param(name = "user[password]") String pwd,
                   @ParamMap Map<String, String> map
    );

    @GET
    Response home(@URL String url);

}
