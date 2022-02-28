package org.kon.oss.listener;

import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 下载文件进度条监听
 *
 * @author kon, created on 2022/2/25T15:30.
 * @version 1.0.0-SNAPSHOT
 */
@Slf4j
public class DownObjectProgressListener implements ProgressListener {

    /**
     * 读取字节数
     */
    private long bytesRead = 0;
    /**
     * 总字节数
     */
    private long totalBytes = -1;
    /**
     * 是否成功
     */
    private boolean succeed = false;

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                log.info("Start to download......");
                break;
            case RESPONSE_CONTENT_LENGTH_EVENT:
                this.totalBytes = bytes;
                log.info("{} bytes in total will be downloaded to a local file", this.totalBytes);
                break;
            case RESPONSE_BYTE_TRANSFER_EVENT:
                this.bytesRead += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesRead * 100.0 / this.totalBytes);
                    log.info("{} bytes have been read at this time, download progress: {}%({}/{})", bytes,
                            percent, this.bytesRead, this.totalBytes);
                } else {
                    log.info("{} bytes have been read at this time, download ratio: unknown ({}/...)", bytes, this.bytesRead);
                }
                break;
            case TRANSFER_COMPLETED_EVENT:
                this.succeed = true;
                log.info("Succeed to download, {} bytes have been transferred in total", this.bytesRead);
                break;
            case TRANSFER_FAILED_EVENT:
                log.info("Failed to download, {} bytes have been transferred", this.bytesRead);
                break;
            default:
                break;
        }
    }

    public boolean isSucceed() {
        return succeed;
    }
}
