package org.kon.oss.listener;

import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 上传文件进度条监听
 *
 * @author kon, created on 2022/2/25T15:32.
 * @version 1.0.0-SNAPSHOT
 */
@Slf4j
public class PushObjectProgressListener implements ProgressListener {

    /**
     * 当前写入字节数
     */
    private long bytesWritten = 0;
    /**
     * 总字节数
     */
    private long totalBytes = -1;
    /**
     * 是否执行成功
     */
    private boolean succeed = false;

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                log.info("Start to upload......");
                break;
            case REQUEST_CONTENT_LENGTH_EVENT:
                this.totalBytes = bytes;
                log.info("{} bytes in total will be uploaded to OSS", this.totalBytes);
                break;
            case REQUEST_BYTE_TRANSFER_EVENT:
                this.bytesWritten += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesWritten * 100.0 / this.totalBytes);
                    log.info("{} bytes have been written at this time, upload progress: {}%({}/{})", bytes,
                            percent, this.bytesWritten, this.totalBytes);
                } else {
                    log.info("{} bytes have been written at this time, upload ratio: unknown ({}/...)", bytes, this.bytesWritten);
                }
                break;
            case TRANSFER_COMPLETED_EVENT:
                this.succeed = true;
                log.info("Succeed to upload, {} bytes have been transferred in total", this.bytesWritten);
                break;
            case TRANSFER_FAILED_EVENT:
                log.info("Failed to upload, {} bytes have been transferred", this.bytesWritten);
                break;
            default:
                break;
        }
    }

    public boolean isSucceed() {
        return succeed;
    }

}
