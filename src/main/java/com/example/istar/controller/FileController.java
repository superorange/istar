package com.example.istar.controller;

import com.example.istar.configuration.MinIoClientConfig;
import com.example.istar.utils.MinioUtil;
import com.example.istar.utils.response.ResEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author tian
 */
@Controller()
@Api(tags = "文件上传")
@RequestMapping("/files")
public class FileController {
    @Resource
    private MinioUtil minioUtil;
    @Resource
    private MinIoClientConfig minIoClientConfig;

    @ApiOperation(value = "minio上传测试")
    @ResponseBody
    @PostMapping("/upload")
    public ResEntity upload(@RequestParam("files") MultipartFile[] multipartFile) throws Exception {
        List<MinioUtil.MinioUploadWrapper> uploadWrappers = minioUtil.uploadFile(multipartFile, minIoClientConfig.getBucketName());
        return ResEntity.ok(uploadWrappers);
    }

//    @ApiOperation(value = "minio下载测试")
//    @GetMapping("/download")
//    public ResponseEntity<byte[]> download(@RequestParam String fileName) {
//        return minioUtil.download(fileName);
//    }
//
//    @ApiOperation(value = "minio创建桶")
//    @PostMapping("/existBucket")
//    public void existBucket(@RequestParam String bucketName) {
//        minioUtil.existBucket(bucketName);
//    }

}
