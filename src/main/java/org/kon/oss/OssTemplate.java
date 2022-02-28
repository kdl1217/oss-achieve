package org.kon.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.kon.oss.listener.DownObjectProgressListener;
import org.kon.oss.listener.PushObjectProgressListener;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 阿里云Oss模板
 *
 * @author kon, created on 2022/2/25T13:54.
 * @version 1.0.0-SNAPSHOT
 */
@Slf4j
public class OssTemplate {

    /**
     * yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com
     */
    private final String endpoint;
    /**
     * 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
     */
    private final String accessKeyId;
    /**
     * 阿里云账号AccessKey密钥
     */
    private final String accessKeySecret;

    /**
     * Oss链接
     */
    private OSS ossClient;

    public OssTemplate(@NonNull String endpoint, @NonNull String accessKeyId, @NonNull String accessKeySecret) {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public OssTemplate build() {
        if (StringUtils.hasLength(this.endpoint) && StringUtils.hasLength(this.accessKeyId)
                && StringUtils.hasLength(this.accessKeySecret)) {
            this.ossClient = new OSSClientBuilder().build(this.endpoint, this.accessKeyId, this.accessKeySecret);
            return this;
        } else {
            log.error("build oss template error, please check [endpoint]、[accessKeyId] or [accessKeySecret]");
            throw new NullPointerException("[endpoint]、[accessKeyId] or [accessKeySecret] is null");
        }
    }

    /**
     * 检查是否连接
     * @return  T/F
     */
    public boolean checkConnected() {
        return this.ossClient != null;
    }

    /**
     * Oss连接
     * @return Oss连接
     */
    public OSS getOssClient() {
        if (!checkConnected()) {
            log.error("ossClient is not connected!!!");
            throw new NullPointerException("ossClient is null");
        }
        return this.ossClient;
    }

    /**
     * 创建桶
     * @param bucketName    桶名称
     */
    public void createBucket(@NonNull String bucketName) {
        try {
            if (!isBucketExist(bucketName)) {
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                // 如果创建存储空间的同时需要指定存储类型和数据容灾类型, 请参考如下代码。
                // 此处以设置存储空间的存储类型为标准存储为例介绍。
                createBucketRequest.setStorageClass(StorageClass.Standard);
                // 数据容灾类型默认为本地冗余存储，即DataRedundancyType.LRS。如果需要设置数据容灾类型为同城冗余存储，请设置为DataRedundancyType.ZRS。
                createBucketRequest.setDataRedundancyType(DataRedundancyType.ZRS);
                // 设置存储空间的权限为公共读，默认为私有。
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                // 创建存储空间。
                getOssClient().createBucket(createBucketRequest);
            } else {
                log.warn("bucket [{}] is exist!!", bucketName);
            }
        } catch (Exception e) {
            log.error("create bucket error", e);
        }
    }

    /**
     * 桶集合
     * @return  桶集合
     */
    public List<Bucket> listBuckets() {
        try {
            return getOssClient().listBuckets();
        } catch (Exception e) {
            log.error("get buckets error", e);
        }
        return null;
    }

    /**
     * 桶是否存在
     * @param bucketName    桶名称
     * @return  T/F
     */
    public boolean isBucketExist(@NonNull String bucketName) {
        try {
            return getOssClient().doesBucketExist(bucketName);
        } catch (Exception e) {
            log.error("check bucket is exist error", e);
        }
        return false;
    }

    /**
     * 获取桶信息
     *
     *      // 地域
     *      bucketInfo.getBucket().getLocation()
     *      // 创建日期
     *      bucketInfo.getBucket().getCreationDate()
     *      // 容灾类型
     *      bucketInfo.getDataRedundancyType()
     *      ……
     * @param bucketName    桶名称
     * @return  桶信息
     */
    public BucketInfo bucketInfo(@NonNull String bucketName) {
        try {
            return getOssClient().getBucketInfo(bucketName);
        } catch (Exception e) {
            log.error("get bucket info error", e);
        }
        return null;
    }

    /**
     * 删除桶
     *      ***Bucket删除后不可恢复，请谨慎操作***
     * @param bucketName    桶名称
     */
    public void deleteBucket(@NonNull String bucketName) {
        try {
            getOssClient().deleteBucket(bucketName);
        } catch (Exception e) {
            log.error("delete bucket error", e);
        }
    }

    /**
     * Object是否存在
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @return T/F
     */
    public boolean isObjectExist(@NonNull String bucketName, @NonNull String objectName) {
        try {
            return getOssClient().doesObjectExist(bucketName, objectName);
        } catch (Exception e) {
            log.error("check objectName is exist error", e);
        }
        return false;
    }

    /**
     * 上传Byte数组
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param content       byte数组
     */
    public void pushStr(@NonNull String bucketName, @NonNull String objectName, @NonNull String content) {
        // 创建PutObject请求。
        pushStr(bucketName, objectName, content, false);
    }

    /**
     * 上传Byte数组
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param content       byte数组
     * @param isListener    是否监听
     */
    public void pushStr(@NonNull String bucketName, @NonNull String objectName, @NonNull String content, boolean isListener) {
        // 创建PutObject请求。
        pushBytes(bucketName, objectName, content.getBytes(), isListener);
    }

    /**
     * 上传Byte数组
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param content       byte数组
     */
    public void pushBytes(@NonNull String bucketName, @NonNull String objectName, @NonNull byte[] content) {
        // 创建PutObject请求。
        pushBytes(bucketName, objectName, content, false);
    }

    /**
     * 上传Byte数组
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param content       byte数组
     * @param isListener    是否监听
     */
    public void pushBytes(@NonNull String bucketName, @NonNull String objectName, @NonNull byte[] content, boolean isListener) {
        try {
            if (!isObjectExist(bucketName, objectName)) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(content));
                if (isListener) {
                    putObjectRequest.withProgressListener(new PushObjectProgressListener());
                }
                // 创建PutObject请求。
                getOssClient().putObject(putObjectRequest);
            } else {
                log.warn("objectName [{}-{}] is exist!!", bucketName, objectName);
            }
        } catch (Exception e) {
            log.error("push bytes error");
        }
    }

    /**
     * 上传网络流
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param url           网络流地址，eg: https://www.aliyun.com/
     */
    public void pushUrl(@NonNull String bucketName, @NonNull String objectName, @NonNull String url) {
        pushUrl(bucketName, objectName, url, false);
    }

    /**
     * 上传网络流
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param url           网络流地址，eg: https://www.aliyun.com/
     * @param isListener    是否监听
     */
    public void pushUrl(@NonNull String bucketName, @NonNull String objectName, @NonNull String url, boolean isListener) {
        try {
            if (!isObjectExist(bucketName, objectName)) {
                InputStream inputStream = new URL(url).openStream();
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
                if (isListener) {
                    putObjectRequest.withProgressListener(new PushObjectProgressListener());
                }
                // 创建PutObject请求。
                getOssClient().putObject(putObjectRequest);
            } else {
                log.warn("objectName [{}-{}] is exist!!", bucketName, objectName);
            }
        } catch (Exception e) {
            log.error("push url error", e);
        }
    }

    /**
     * 上传文件
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param filePath      文件路径，eg: D:\\localpath\\examplefile.txt
     */
    public void pushFile(@NonNull String bucketName, @NonNull String objectName, @NonNull String filePath) {
        pushFile(bucketName, objectName, filePath, false);
    }

    /**
     * 上传文件
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param filePath      文件路径，eg: D:\\localpath\\examplefile.txt
     * @param isListener    是否监听
     */
    public void pushFile(@NonNull String bucketName, @NonNull String objectName, @NonNull String filePath, boolean isListener) {
        try {
            if (!isObjectExist(bucketName, objectName)) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new FileInputStream(filePath));
                if (isListener) {
                    putObjectRequest.withProgressListener(new PushObjectProgressListener());
                }
                // 创建PutObject请求。
                getOssClient().putObject(putObjectRequest);
            } else {
                log.warn("objectName [{}-{}] is exist!!", bucketName, objectName);
            }
        } catch (Exception e) {
            log.error("push file error", e);
        }
    }

    /**
     * 删除文件
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     */
    public void deleteObject(@NonNull String bucketName, @NonNull String objectName) {
        try {
            getOssClient().deleteObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("delete object error", e);
        }
    }

    /**
     * 流式下载
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @return  数据集合
     */
    public List<String> downStream(@NonNull String bucketName, @NonNull String objectName) {
        return downStream(bucketName, objectName, false);
    }

    /**
     * 流式下载
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param isListener    是否监听
     * @return  数据集合
     */
    public List<String> downStream(@NonNull String bucketName, @NonNull String objectName, boolean isListener) {
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
            if (isListener) {
                getObjectRequest.withProgressListener(new DownObjectProgressListener());
            }
            // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
            OSSObject ossObject = getOssClient().getObject(getObjectRequest);
            // 读取文件内容。
            BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
            List<String> readList = new ArrayList<>();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                readList.add(line);
            }
            // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            reader.close();
            // ossObject对象使用完毕后必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            ossObject.close();
            return readList;
        } catch (Exception e) {
            log.error("down object stream error", e);
        }
        return null;
    }

    /**
     * 文件路径指定下载
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param filePath      文件路径：eg: D:\\localpath\\examplefile.txt
     */
    public void downPath(@NonNull String bucketName, @NonNull String objectName, String filePath) {
        downPath(bucketName, objectName, filePath, false);
    }

    /**
     * 文件路径指定下载
     * @param bucketName    桶名称
     * @param objectName    Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @param filePath      文件路径：eg: D:\\localpath\\examplefile.txt
     * @param isListener    是否监听
     */
    public void downPath(@NonNull String bucketName, @NonNull String objectName, String filePath, boolean isListener) {
        try {
            // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
            // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
            if (isListener) {
                getObjectRequest.withProgressListener(new DownObjectProgressListener());
            }
            getOssClient().getObject(getObjectRequest, new File(filePath));
        } catch (Exception e) {
            log.error("down object stream error", e);
        }
    }


}
