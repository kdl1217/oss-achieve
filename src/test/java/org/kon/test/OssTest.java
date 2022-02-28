package org.kon.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kon.oss.OssTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Oss Test
 *
 * @author kon, created on 2022/2/28T09:46.
 * @version 1.0.0-SNAPSHOT
 */
@Slf4j
@SpringBootTest
public class OssTest {

    @Autowired
    private OssTemplate ossTemplate;

    public static final String BUCKET_NAME = "ic-users";

    @Test
    @Disabled
    public void push() {
        this.ossTemplate.pushStr(BUCKET_NAME, "Kong/test.txt", "Hello World", true);
    }

    @Test
    @Disabled
    public void pushUrl() {
        this.ossTemplate.pushUrl(BUCKET_NAME, "Kong/url.html", "https://www.baidu.com", true);
    }

    @Test
    @Disabled
    public void down() {
        List<String> strings = this.ossTemplate.downStream(BUCKET_NAME, "Kong/test.txt", true);
        log.info("down stream {}", strings);
    }
}
