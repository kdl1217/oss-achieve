package org.kon.config;

import org.kon.oss.OssTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Oss配置
 *
 * @author kon, created on 2022/2/25T16:41.
 * @version 1.0.0-SNAPSHOT
 */
@Configuration
public class OssConfiguration {

    @Value("${oss.endpoint}")
    String endpoint;

    @Value("${oss.accessKeyId}")
    String accessKeyId;

    @Value("${oss.accessKeySecret}")
    String accessKeySecret;

    @Value("${oss.isPrivate}")
    boolean isPrivate;

    @Bean
    public OssTemplate getOssTemplate() {
        return new OssTemplate(this.endpoint, this.accessKeyId, this.accessKeySecret)
                .setPrivate(isPrivate)
                .build();
    }
}
