package com.example.istar.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.RedisConst;
import com.example.istar.common.Roles;
import com.example.istar.configuration.MinIoClientConfig;
import com.example.istar.dto.impl.PageWrapper;
import com.example.istar.dto.impl.UserWrapperDto;
import com.example.istar.entity.UserEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.model.PageModel;
import com.example.istar.model.LoginModel;
import com.example.istar.model.UserUpdateModel;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.utils.*;
import com.example.istar.utils.response.ErrorException;
import com.example.istar.utils.response.ErrorMsg;
import com.example.istar.utils.response.ResEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tian
 * @since 2022-04-18
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private MinIoClientConfig minIoClientConfig;
    @Resource
    private UserServiceImpl userService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private MinioUtil minioUtil;
    @Resource
    private UserUtil userUtil;

    @PostMapping("/login")
    @ApiOperation(value = "注册/登录", notes = "用户注册,或者登录")
    public ResEntity<UserWrapperDto> login(@RequestBody LoginModel model) throws ErrorException {
        model.check();
        ///1, 校验验证码
        String redisCode = redisUtil.getCacheObject(RedisConst.AUTH_CODE_BY_KEY + model.getKey());
        if (redisCode != null && redisCode.equals(model.getCode())) {
            ///校验成功,删除验证码
            redisUtil.deleteObject(RedisConst.AUTH_CODE_BY_KEY + model.getKey());
            UserEntity userEntity;
            if (RegexTool.isEmail(model.getKey())) {
                userEntity = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getEmail, model.getKey()));
            } else if (RegexTool.isMobiles(model.getKey())) {
                userEntity = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getMobile, model.getKey()));
            } else {
                throw ErrorException.wrap(HttpStatus.BAD_REQUEST, "手机号或邮箱格式不正确");
            }
            ///新用户
            if (userEntity == null) {
                userEntity = generateUser(model);
                boolean save = userService.save(userEntity);
                if (!save) {
                    return ResEntity.fail(ErrorMsg.DATABASE_ERROR);
                }
            }
            String token = SafeUtil.generateToken(userEntity.getUuid());
            if (token == null) {
                return ResEntity.fail("生成token失败，请稍后再试");
            }
            LoginUser loginUser = new LoginUser(userEntity, Arrays.asList(userEntity.getRoles().split(",")));
            redisUtil.setCacheObject(RedisConst.USER_INFO_BY_UUID + userEntity.getUuid(), loginUser, SafeUtil.EXPIRE_TIME, SafeUtil.TIME_UNIT);
            return ResEntity.ok(new UserWrapperDto(token, userEntity));
        }
        return ResEntity.fail(ErrorMsg.CODE_ERROR);


    }

    private UserEntity generateUser(LoginModel model) {
        UserEntity userEntity = new UserEntity();
        if (RegexTool.isEmail(model.getKey())) {
            userEntity.setEmail(model.getKey());
        } else if (RegexTool.isMobiles(model.getKey())) {
            userEntity.setMobile(model.getKey());
        }
        userEntity.setUuid(UUID.randomUUID().toString(false));
        userEntity.setCreateTime(System.currentTimeMillis());
        userEntity.setModifyTime(System.currentTimeMillis());
        userEntity.setRoles(Roles.SYS_USER);
        userEntity.setStatus(Roles.PUBLIC_SEE);
        userEntity.setPoint(0L);
        userEntity.setBalance(0.0);
        return userEntity;
    }


    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("")
    public ResEntity<PageWrapper<UserEntity>> getUsers(PageModel pageModel) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderBy(true, pageModel.isAsc(), UserEntity::getId);
        Page<UserEntity> page = new Page<>(pageModel.getCurrentIndex(), pageModel.getCurrentCount());
        Page<UserEntity> entityPage = userService.page(page, queryWrapper);
        return ResEntity.ok(PageWrapper.wrap(entityPage));
    }

    @ApiOperation(value = "获取单个用户信息", notes = "获取单个用户信息")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("/{uuid}")
    public ResEntity<UserEntity> getUser(@PathVariable String uuid) {
        return ResEntity.ok(userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUuid, uuid)));
    }

    /**
     * 更新用户信息
     *
     * @return R<User>
     */
    @PatchMapping("")
    @ApiOperation(value = "用户更新", notes = "用户更新", response = UserEntity.class)
    public ResEntity<UserEntity> updateUser(UserUpdateModel model) throws Exception {
        String uuid = LoginUser.getUuidAndThrow();
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUuid, uuid);
        UserEntity userEntity = userService.getOne(queryWrapper);
        if (userEntity == null) {
            return ResEntity.fail(ErrorMsg.USER_NOT_EXIST);
        }
        if (model.getAvatar() != null) {
            MinioUtil.MinioUploadWrapper minioUploadWrapper = minioUtil.uploadFile(model.getAvatar(),minIoClientConfig.getPictureBucketName());
            userEntity.setAvatar(minioUploadWrapper.getFileBucketName());
        }
        userEntity.setNickName(model.getNickName());
        userEntity.setModifyTime(System.currentTimeMillis());
        boolean id = userService.updateById(userEntity);
        if (id) {
            userUtil.setNickNameByUuid(userEntity.getUuid(), userEntity.getNickName());
            userUtil.setAvatarByUuid(userEntity.getUuid(), userEntity.getAvatar());
        }
        return id ? ResEntity.ok() : ResEntity.fail(5336, ErrorMsg.DATABASE_ERROR);
    }

    @DeleteMapping("/{uuid}")
    @ApiOperation(value = "用户销户", notes = "用户销户")
    public ResEntity<Boolean> deleteUser(@PathVariable String uuid) throws ErrorException {
        String loginUuid = LoginUser.getUuidAndThrow();
        if (!loginUuid.equals(uuid) && !Roles.isSuperAdmin()) {
            return ResEntity.fail(ErrorMsg.FORBIDDEN);
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUuid, uuid);
        queryWrapper.select(UserEntity::getId);
        UserEntity one = userService.getOne(queryWrapper);
        if (ObjectUtil.isNull(one)) {
            return ResEntity.fail(ErrorMsg.USER_NOT_EXIST);
        }
        boolean b = userService.removeById(one.getId());
        return b ? ResEntity.ok() : ResEntity.fail(5337, ErrorMsg.DATABASE_ERROR);
    }


}
