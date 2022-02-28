package org.kon.annotation;

import org.kon.config.OssConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用OSS连接
 *
 * @author kon, created on 2022/2/28T17:28.
 * @version 1.0.0-SNAPSHOT
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(OssConfiguration.class)
@Documented
public @interface EnableOssClient {
}
