package com.example.istar.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.common.Const;
import com.example.istar.common.Roles;
import com.example.istar.entity.PictureEntity;
import com.example.istar.service.impl.PicturesServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tian
 */
@Component
public class PictureUtil {
    @Resource
    private PicturesServiceImpl picturesService;
    @Resource
    private MinioUtil minioUtil;

    public List<PictureEntity> getEntities(List<String> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            return null;
        }
        HashSet<String> set = new HashSet<>(ids);
        LambdaQueryWrapper<PictureEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PictureEntity::getPicId, set);
        queryWrapper.eq(PictureEntity::getStatus, Roles.PUBLIC_SEE);
        return picturesService.list(queryWrapper);
    }


    public List<PictureEntity> getEntitiesByMinioWrapper(List<MinioUtil.MinioUploadWrapper> wrappers, String uuid) {
        if (ObjectUtil.isEmpty(wrappers)) {
            return null;
        }
        return wrappers.stream().map(f -> {
            PictureEntity pictureEntity = new PictureEntity();
            pictureEntity.setUuid(uuid);
            pictureEntity.setPicId(f.getFileId());
            pictureEntity.setPicFull(f.getFileBucketName());
            pictureEntity.setPicType(Const.PICTURE_TYPE_TOPIC);
            pictureEntity.setStatus(Roles.PUBLIC_SEE);
            pictureEntity.setCreateTime(System.currentTimeMillis());
            return pictureEntity;
        }).collect(Collectors.toList());
    }


    public void remove(List<String> ids) {
        HashSet<String> set = new HashSet<>(ids);
        LambdaQueryWrapper<PictureEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PictureEntity::getPicId, set);
        picturesService.remove(queryWrapper);
    }
}
