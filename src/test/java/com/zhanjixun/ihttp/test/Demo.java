package com.zhanjixun.ihttp.test;

import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.ICookie;
import com.zhanjixun.ihttp.Response;
import org.apache.commons.httpclient.Cookie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Map;

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
        Response index = gitee.index();
        if (index.isOK()) {
            String token = index.getDocument().select("[name='authenticity_token']").get(0).val();
            Map<String, String> map = Maps.newHashMap();
            map.put("user[login]", email);
            map.put("user[password]", password);
            Response login = gitee.login(token, map);
            if (login.isRedirect()) {
                String href = login.getDocument().select("a").get(0).attr("href");
                Response home = gitee.home(href);
                System.out.println("欢迎您：" + home.getDocument().select(".git-user-name-link").get(0).text());

                ICookie[] cookies = gitee.getCookies();
                System.out.println(Arrays.toString(cookies));
            }


        }
    }
}
