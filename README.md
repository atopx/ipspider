
# Slide

## 项目说明

> 使用`Java`破解 [ip.rtbasia.com](https://ip.rtbasia.com "ip.rtbasia.com")
> 使用`SpringBoot`构建服务api

## 开发环境

- maven==3.6.3
- java==1.8
- IDEA==2019.3

## 依赖包

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>3.141.59</version>
    </dependency>
    <dependency>
        <groupId>net.lightbody.bmp</groupId>
        <artifactId>browsermob-core</artifactId>
        <version>2.1.5</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

## 核心代码思路

参考：[滑动验证码通用解决方案](http://blog.itmeng.top/20160102/滑动验证码通用解决方案/)

## 运行项目

1. 下载项目
    ```shell script
    git clone git@github.com:yanmengfei/ipspider.git
    ```

2. 修改配置文件， `./src/main/resources/*.properties`

3. 开发环境运行
    ```shell script
    mvn spring-boot:run
    ```

4. 生产环境运行
    ```shell script
    mvn clean
    mvn package
    java -jar ./target/slide-0.0.1.jar --spring.profiles.active=prod
    ```

## 接口说明

1. GET => `/ping`

   ```json
   {
     "headless": false,
     "driver": "/Users/meng/meng_config/chromedriver",
     "timeout": 10
   }
   ```

2. GET => `/query` => QueryString: `?ip=39.100.112.108`

   ```json
   {
     "country": "中国",
     "province": "河南",
     "city": "郑州",
     "org": "移动",
     "district": "中原区  ",
     "ip_type": "专用出口"
   }
   ```

3. POST => `/search` => Json:`{"ips": ["39.100.112.108"]}`

   ```json
   {
     "117.158.142.120": {
       "country": "中国",
       "province": "河南",
       "city": "郑州",
       "org": "移动",
       "district": "中原区  ",
       "ip_type": "专用出口"
     }
   }
   ```

   