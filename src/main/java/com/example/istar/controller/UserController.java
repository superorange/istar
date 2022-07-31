package com.example.istar.controller;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.istar.common.RedisConst;
import com.example.istar.common.Roles;
import com.example.istar.dto.impl.UserWrapper;
import com.example.istar.entity.UserEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.dto.impl.PageModel;
import com.example.istar.dto.impl.LoginModel;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

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


    @PostMapping("/login")
    @ApiOperation(value = "注册/登录", notes = "用户注册,或者登录")
    public R<UserWrapper> preRegister(@RequestBody LoginModel model) throws Exp {
        if (model.isBadFormat()) {
            throw Exp.from(ResultCode.ERROR_PARAM);
        }
        ///1, 校验验证码
        String redisCode = redisUtil.getCacheObject(RedisConst.REDIS_LOGIN_CODE + model.getData());
        if (redisCode != null && redisCode.equals(model.getCode())) {
            ///检验成功，删除验证码
            redisUtil.deleteObject(RedisConst.REDIS_LOGIN_CODE + model.getData());
            UserEntity userEntity = null;
            if (RegexTool.isEmail(model.getData())) {
                userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("email", model.getData()));
            } else if (RegexTool.isMobiles(model.getData())) {
                userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("mobile", model.getData()));
            }
            ///新用户
            if (userEntity == null) {
                userEntity = generateUser(model);
                boolean save = userService.save(userEntity);
                if (!save) {
                    throw Exp.from(ResultCode.REGISTER_ERROR);
                }
            }
            String token = SafeUtil.generateToken(userEntity.getUuid());
            if (token == null) {
                throw Exp.from(ResultCode.FAILED);
            }
            LoginUser loginUser = new LoginUser(userEntity, Arrays.asList(userEntity.getRoles().split(",")));
            redisUtil.setCacheObject(RedisConst.REDIS_LOGIN_INFO + userEntity.getUuid(), loginUser, SafeUtil.EXPIRE_TIME, SafeUtil.TIME_UNIT);
            return R.ok(new UserWrapper(token, userEntity));
        }
        throw Exp.from(ResultCode.CODE_ERROR);


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

    ///TODO hasRole 默认增加ROLE_前缀
//    @PreAuthorize("hasAuthority('ROLE_ROOT1')")
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("")
    public R<List<UserEntity>> getUsers(PageModel pageModel) {
        return R.ok(userService.queryUsers(pageModel));
    }

    @ApiOperation(value = "获取单个用户信息", notes = "获取单个用户信息")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("/{uuid}")
    public R<UserEntity> getUser(@PathVariable String uuid) {
        return R.ok(userService.getOne(new QueryWrapper<UserEntity>().eq("uuid", uuid)));
    }

    /**
     * 更新用户信息
     *
     * @return R<User>
     */
    @PatchMapping("")
    @ApiOperation(value = "用户更新", notes = "用户更新", response = UserEntity.class)
    public R<UserEntity> updateUser() {
        return R.ok();
    }

    @DeleteMapping("/{uuid}")
    @ApiOperation(value = "用户销户", notes = "用户销户")
    public R<Boolean> deleteUser(@PathVariable String uuid) throws Exp {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //是自己或者是超级管理员，则可以删除
        if (loginUser.getUserEntity().getUuid().equals(uuid) || loginUser.getUserEntity().getRoles().equals(Roles.SYS_SUPER_ADMIN)) {
            QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("uuid", loginUser.getUserEntity().getUuid());
            UserEntity one = userService.getOne(wrapper);
            if (one != null) {
                one.setStatus(-1);
                boolean update = userService.updateById(one);
                redisUtil.deleteObject(RedisConst.REDIS_LOGIN_INFO + loginUser.getUserEntity().getUuid());
                return update ? R.ok() : R.fail(ResultCode.OPERATION_FAILED);
            }
            return R.fail();

        }
        ///不是抛出异常
        throw Exp.from(ResultCode.OPERATION_FORBIDDEN);
    }


}
