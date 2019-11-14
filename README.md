# ihttp - 基于注解的Java网络编程

[ihttp]( https://github.com/zhanjixun/ihttp ) 使用注解方式配置Java网络编程，避免大量编写构造请求的代码，更优雅的编码。

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.zhanjixun/ihttp/badge.svg)](https://mvnrepository.com/artifact/com.github.zhanjixun/ihttp)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## 快速开始

```xml
<dependency>
    <groupId>com.github.zhanjixun</groupId>
    <artifactId>ihttp</artifactId>
    <version>19.10</version>
</dependency>
```

### 发送一个GET请求

```java
//就像mybatis一样定义一个接口映射http请求
public interface Mapper {
    
    @GET
    @URL("https://github.com/")
    Response openBaidu();
    
}

//调用
Mapper mapper = IHTTP.getMapper(Mapper.class);
Response response = mapper.openBaidu();
//获取Response对象进行返回结果处理
```

> 目前支持的http请求方法有：`@GET、@POST、@PUT、@DELETE`

### 携带请求参数

```java
//请求参数：用于方法体或方法参数上
@Param(name = "userName", value = "zhanjixun")

//请求参数：用于方法体上，自动生成时间戳请求参数
@TimestampParam(name = "_", unit = TimeUnit.MILLISECONDS)

//请求参数：用于方法体上，自动生成随机字符串
@RandomParam(name = "key", length = 6, chars = "0123456789")
```

### 携带请求头

```java
//请求头：用于方法体或方法参数上
@Header(name = "User-Agent", value = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36")

//语义化请求注解：用于方法体或方法参数上，携带相关请求头
@Accept
@AcceptEncoding
@AcceptLanguage
@Origin
@Referer
@UserAgent
```

### 携带请求体

```java
//请求体：用于方法体后方法参数上
@StringBody("{\"cat\":{\"name\":\"Matilda\"}}")

//请求体：用于方法参数上，将对象直接转成json作为请求体发送
@StringBodyObject
```

## 关于Cookie的使用

IHTTP中默认开启为每个Mapper启用Cookie，Mapper接口继承`CookiesStore`将获取Cookie操作的能力。如下：

```java
interface Mapper extends CookiesStore {
    @GET
    @URL("https://github.com/")
    Response openBaidu();
}

Mapper mapper = IHTTP.getMapper(Mapper.class);
Response response = mapper.openBaidu();
mapper.getCookies().forEach(System.out::println);

```

## 在Spring中使用

在上面的示例中都是直接使用`IHTTP.getMapper(Mapper.class);`来获取Mapper实例，如果是在Spring环境中使用，可以使用Spring对象容器来托管Mapper。只需要在spring-context.xml中添加扫描bean，然后就可以注入实例使用。

```xml
<!--扫描ittp映射接口-->
<bean class="com.zhanjixun.ihttp.spring.MapperScanner">
    <!--mapper接口所在包-->
    <property name="basePackage" value="com.zhanjixun.ihttp.test"/>
</bean>
```

```java
//在需要使用的地方注入Mapper
@Autowired
private Mapper mapper;

```

