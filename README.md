# ihttp - 基于注解的Java网络编程

----------

[ihttp]( https://github.com/zhanjixun/ihttp ) 使用注解方式配置Java网络编程，避免大量编写构造请求的代码，更优雅的编码。

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.zhanjixun/ihttp/badge.svg)](https://mvnrepository.com/artifact/com.github.zhanjixun/ihttp)[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

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
    @URL("http://www.baidu.com")
    Response openBaidu();
    
}

//调用
Mapper mapper = IHTTP.getMapper(Mapper.class);
Response response = mapper.openBaidu();
```

