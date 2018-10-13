package com.zhanjixun.ihttp.test.gitee;

import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.Response;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Map;

@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class Demo {

    //填写您的码云帐号信息测试
    @Value("${gitee.username}")
    private String email;
    @Value("${gitee.password}")
    private String password;

    @Autowired
    private Gitee gitee;

    /**
     * 此测试用例用来演示IHTTP的使用，演示模拟登录gitee(码云),请在user.properties填写您的码云帐号信息测试
     */
    @Test
    public void test() {
        gitee.index().ok(index -> {
            String token = index.getDocument().select("[name='authenticity_token']").get(0).val();
            Map<String, String> map = Maps.newHashMap();
            map.put("user[login]", email);
            map.put("user[password]", password);
            gitee.login(token, map).redirect(login -> {
                String href = login.getDocument().select("a").get(0).attr("href");
                Response home = gitee.home(href);
                System.out.println("欢迎您：" + home.getDocument().select(".git-user-name-link").get(0).text());

                Arrays.stream(gitee.getCookies()).forEach(System.out::println);
            });
        }, index -> log.warn("首页index状态码：" + index.getStatus()));
    }
}
