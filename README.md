# ihttp - 基于注解方式的网页爬取工具

----------

## 一、前言 ##
   在用java进行网页爬取的时候，经常需要繁琐的代码来构建http请求，例如在模拟登录后并获取数据的过程，代码会变得十分臃肿难于维护，需要写大量的代码来发送一个请求，但是构建http请求的代码几乎都是相同的。如下代码所示：

```
HttpClient httpClient = new HttpClient();
Header ua = new Header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");

//构建http请求
GetMethod getMethod = new GetMethod("https://gitee.com/login");
getMethod.addRequestHeader(ua);

int status = httpClient.executeMethod(getMethod);

if (status == 200) {
    String response = getMethod.getResponseBodyAsString();
    //逻辑处理部分
    String token = Jsoup.parse(response).select("[name='authenticity_token']").get(0).val();

    PostMethod postMethod = new PostMethod("https://gitee.com/login");
    postMethod.addParameter("utf8", "✓");
    postMethod.addParameter("redirect_to_url", "");
    postMethod.addParameter("captcha", "");
    postMethod.addParameter("user[remember_me]", "");
    postMethod.addParameter("commit", "登 录");
    postMethod.addParameter("authenticity_token", token);
    postMethod.addParameter("user[login]", "xxx");
    postMethod.addParameter("user[password]", "xxx");

    postMethod.addRequestHeader(ua);
    int i = httpClient.executeMethod(postMethod);
    if (i == 302) {
        Header location = postMethod.getResponseHeader("Location");
        System.out.println(location);
        //后续处理
    }
}
```
这里只是简单的两个请求，如果一些比较复杂的网页过程，需要发送n多个请求的时候，整个代码就会变得编写麻烦而且极难维护。那么在这种情况之下，我就编写了这个工具。
## 二、简介 ##
 ihttp是一个基于注解配置http请求的工作。分层次，使用注解快速构建http请求，让我们做到可以只关注http请求本身和逻辑处理代码，不再需要花时间编码大量的代码来发送http请求。
 例如将上例改造：
 
使用一个接口来配置http的请求
```
@Logger
@URL("https://gitee.com")
@UserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36")
public interface Gitee {

@GET
@URL("/login")
Response index();

@POST
@URL("/login")
@UserAgent("httpclient")
@Param(name = "utf8", value = "✓")
@Param(name = "redirect_to_url")
@Param(name = "captcha")
@Param(name = "user[remember_me]")
@Param(name = "commit", value = "登 录")
Response login(@Param(name = "authenticity_token") String token, @Param(name = "user[login]") String email, @Param(name = "user[password]") String pwd);

@GET
Response home(@URL String url);

}
```
如何调用
```
Gitee gitee = IHTTP.getMapper(Gitee.class);
Response index = gitee.index();
if (index.isOK()) {
    String token = index.getDocument().select("[name='authenticity_token']").get(0).val();
    Response login = gitee.login(token, email, password);
    if (login.isRedirect()) {
        String href = login.getDocument().select("a").get(0).attr("href");
        Response home = gitee.home(href);
        System.out.println("欢迎您：" + home.getDocument().select(".git-user-name-link").get(0).text());
    }
}
```
在使用了ihttp之后就将代码分成两个部分，这样可以清晰的分开不同部分的业务，让我们只关注每部分的细节。

 1. 一个接口：负责“收集”http请求的内容
 2. 调用过程：负责逻辑处理
