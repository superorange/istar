package com.example.istar.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.common.Const;
import com.example.istar.common.Roles;
import com.example.istar.entity.PictureEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.service.impl.PicturesServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
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

    public List<PictureEntity> getEntities(Set<String> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            return null;
        }
        LambdaQueryWrapper<PictureEntity> picturesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        picturesLambdaQueryWrapper.in(PictureEntity::getPicId, ids);
        picturesLambdaQueryWrapper.eq(PictureEntity::getStatus, Roles.PUBLIC_SEE);
        return picturesService.list(picturesLambdaQueryWrapper).stream().peek(pictures -> {
            ///添加图片地址前缀
            pictures.setPicUrl(minioUtil.getBasisUrl() + pictures.getPicUrl());
        }).collect(Collectors.toList());
    }


    public List<PictureEntity> getEntitiesByMinioWrapper(List<MinioUtil.MinioUploadWrapper> wrappers) {
        if (ObjectUtil.isEmpty(wrappers)) {
            return null;
        }

        return wrappers.stream().map(f -> {
            PictureEntity pictureEntity = new PictureEntity();
            try {
                pictureEntity.setUuid(LoginUser.getUuidAndThrow());
            } catch (Exp e) {
                throw new RuntimeException(e);
            }
            pictureEntity.setPicId(f.getFileId());
            pictureEntity.setPicUrl(f.getFileBucketName());
            pictureEntity.setPicName(f.getFileOriginName());
            pictureEntity.setPicType(Const.PICTURE_TYPE_TOPIC);
            pictureEntity.setStatus(0);
            pictureEntity.setCreateTime(System.currentTimeMillis());
            return pictureEntity;
        }).collect(Collectors.toList());
    }
}
