package com.example.istar.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.common.RedisConst;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.PageWrapperDto;
import com.example.istar.dto.impl.UserWrapperDto;
import com.example.istar.entity.UserEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.model.PageModel;
import com.example.istar.model.LoginModel;
import com.example.istar.model.UserUpdateModel;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.utils.*;
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
    private UserServiceImpl userService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private MinioUtil minioUtil;

    @PostMapping("/login")
    @ApiOperation(value = "注册/登录", notes = "用户注册,或者登录")
    public Res<UserWrapperDto> login(@RequestBody LoginModel model) throws Exp {
        model.check();
        ///1, 校验验证码
        String redisCode = redisUtil.getCacheObject(RedisConst.AUTH_CODE_BY_KEY + model.getData());
        if (redisCode != null && redisCode.equals(model.getCode())) {
            ///校验成功,删除验证码
            redisUtil.deleteObject(RedisConst.AUTH_CODE_BY_KEY + model.getData());
            UserEntity userEntity;
            if (RegexTool.isEmail(model.getData())) {
                userEntity = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getEmail, model.getData()));
            } else if (RegexTool.isMobiles(model.getData())) {
                userEntity = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getMobile, model.getData()));
            } else {
                throw Exp.from(HttpStatus.BAD_REQUEST, 4001, "手机号或邮箱格式不正确");
            }
            ///新用户
            if (userEntity == null) {
                userEntity = generateUser(model);
                boolean save = userService.save(userEntity);
                if (!save) {
                    return Res.fail(ErrorMsg.DATABASE_ERROR);
                }
            }
            String token = SafeUtil.generateToken(userEntity.getUuid());
            if (token == null) {
                return Res.fail("生成token失败，请稍后再试");
            }
            LoginUser loginUser = new LoginUser(userEntity, Arrays.asList(userEntity.getRoles().split(",")));
            redisUtil.setCacheObject(RedisConst.USER_INFO_BY_UUID + userEntity.getUuid(), loginUser, SafeUtil.EXPIRE_TIME, SafeUtil.TIME_UNIT);
            return Res.ok(new UserWrapperDto(token, userEntity));
        }
        return Res.fail(ErrorMsg.CODE_ERROR);


    }

    private UserEntity generateUser(LoginModel model) {
        UserEntity userEntity = new UserEntity();
        if (RegexTool.isEmail(model.getData())) {
            userEntity.setEmail(model.getData());
        } else if (RegexTool.isMobiles(model.getData())) {
            userEntity.setMobile(model.getData());
        }
        userEntity.setUuid(UUID.randomUUID().toString(false));
        userEntity.setCreateTime(System.currentTimeMillis());
        userEntity.setModifyTime(System.currentTimeMillis());
        userEntity.setRoles(Roles.SYS_USER);
        userEntity.setStatus(0);
        userEntity.setPoint(0L);
        userEntity.setBalance(0.0);
        return userEntity;
    }

    /**
     * /TODO hasRole 默认增加ROLE_前缀
     * <p>
     * //@PreAuthorize("hasAuthority('ROLE_ROOT1')")
     */
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("")
    public Res<PageWrapperDto<UserEntity>> getUsers(PageModel pageModel) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderBy(true, pageModel.isAsc(), UserEntity::getId);
        Page<UserEntity> page = new Page<>(pageModel.getCurrentIndex(), pageModel.getCurrentCount());
        Page<UserEntity> entityPage = userService.page(page, queryWrapper);
        return Res.ok(PageWrapperDto.wrap(entityPage));
    }

    @ApiOperation(value = "获取单个用户信息", notes = "获取单个用户信息")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("/{uuid}")
    public Res<UserEntity> getUser(@PathVariable String uuid) {
        return Res.ok(userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUuid, uuid)));
    }

    /**
     * 更新用户信息
     *
     * @return R<User>
     */
    @PatchMapping("")
    @ApiOperation(value = "用户更新", notes = "用户更新", response = UserEntity.class)
    public Res<UserEntity> updateUser(UserUpdateModel model) throws Exception {
        String uuid = LoginUser.getUuidAndThrow();
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUuid, uuid);
        UserEntity userEntity = userService.getOne(queryWrapper);
        if (userEntity == null) {
            return Res.fail(ErrorMsg.USER_NOT_EXIST);
        }
        if (model.getAvatar() != null) {
            MinioUtil.MinioUploadWrapper minioUploadWrapper = minioUtil.uploadFile(model.getAvatar());
            userEntity.setAvatar(minioUploadWrapper.getFileId());
        }
        userEntity.setNickName(model.getNickName());
        userEntity.setModifyTime(System.currentTimeMillis());
        boolean id = userService.updateById(userEntity);
        return id ? Res.ok() : Res.fail(5336, ErrorMsg.DATABASE_ERROR);
    }

    @DeleteMapping("/{uuid}")
    @ApiOperation(value = "用户销户", notes = "用户销户")
    public Res<Boolean> deleteUser(@PathVariable String uuid) throws Exp {
        String loginUuid = LoginUser.getUuidAndThrow();
        if (!loginUuid.equals(uuid) && !Roles.isSuperAdmin()) {
            return Res.fail(ErrorMsg.OPERATION_FORBIDDEN);
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUuid, uuid);
        queryWrapper.select(UserEntity::getId);
        UserEntity one = userService.getOne(queryWrapper);
        if (ObjectUtil.isNull(one)) {
            return Res.fail(ErrorMsg.USER_NOT_EXIST);
        }
        boolean b = userService.removeById(one.getId());
        return b ? Res.ok() : Res.fail(5337, ErrorMsg.DATABASE_ERROR);
    }


}
