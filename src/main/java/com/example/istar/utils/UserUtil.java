package com.example.istar.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.common.RedisConst;
import com.example.istar.entity.UserEntity;
import com.example.istar.service.impl.UserServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserUtil {
    @Resource
    private UserServiceImpl userService;
    @Resource
    private RedisUtil redisUtil;

    public String getAvatarByUuid(String uuid) {
        String re = redisUtil.getCacheObject(RedisConst.USER_AVATAR_BY_UUID + uuid);
        ///如果为空则从数据库中获取
        if (StrUtil.isEmpty(re)) {
            UserEntity user = userService.getOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getUuid, uuid));
            if (user != null) {
                re = user.getAvatar();
                redisUtil.setCacheObject(RedisConst.USER_AVATAR_BY_UUID + uuid, re);
            }
        }
        return re;

    }


    public String getNickNameByUuid(String uuid) {
        String re = redisUtil.getCacheObject(RedisConst.USER_NICK_NAME_BY_UUID + uuid);
        ///如果为空则从数据库中获取
        if (StrUtil.isEmpty(re)) {
            UserEntity user = userService.getOne(new LambdaQueryWrapper<UserEntity>()
                    .select(UserEntity::getAvatar)
                    .eq(UserEntity::getUuid, uuid));
            if (user != null) {
                re = user.getAvatar();
                redisUtil.setCacheObject(RedisConst.USER_AVATAR_BY_UUID + uuid, re);
            }
        }
        return re;
    }
}
