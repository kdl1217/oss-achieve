# oss-achieve

[![java](https://img.shields.io/badge/java-1.8-brightgreen.svg?style=flat&logo=java)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![gradle](https://img.shields.io/badge/gradle-7.2-brightgreen.svg?style=flat&logo=gradle)](https://docs.gradle.org/6.7/userguide/installation.html)
[![release](https://img.shields.io/badge/release-1.0-blue.svg)]()

阿里云OSS服务Java实现

---

## 一、服务说明
> 通过注入OssTemplate到Spring进行管理。即可使用ossTemplate对oss进行一系列操作。

---
## 二、使用说明

- 添加yaml配置
````yaml
# Oss settings
oss:
  endpoint: https://oss-cn-hangzhou.aliyuncs.com
  accessKeyId: WJ0125KNWTSW
  accessKeySecret: 92eb026ba2c3215f36e902
````
- 在SpringBoot启动类上添加注解
```java
@EnableOssClient
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

---
### 服务包括内容
- 检测桶
- 新建桶
- 获取桶信息
- 删除桶
- 上传文本
- 上传字节
- 上传网络流（URL）
- 上传百分比进度监听
- 删除数据
- 流式下载
- 文件路径指定下载
- 下载百分比进度监听