package com.example.istar.configuration;

import com.example.istar.utils.MinioUtil;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

/**
 * @author tian
 */
@Data
@Component
public class MinIoClientConfig {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.bucketName}")
    private String bucketName;
    @Value("${minio.videoBucketName}")
    private String videoBucketName;
    @Value("${minio.pictureBucketName}")
    private String pictureBucketName;


    @Resource
    @Lazy
    private MinioClient minioClient;

    /**
     * 注入minio 客户端
     *
     * @return MinioClient
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    MinioUtil minioUtils() {
        try {
            return new MinioUtil(endpoint, 20, 20, minioClient, bucketName, pictureBucketName, videoBucketName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
