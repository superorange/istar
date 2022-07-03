package com.example.istar.controller;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.istar.common.RedisConst;
import com.example.istar.common.Roles;
import com.example.istar.entity.User;
import com.example.istar.handler.LoginUser;
import com.example.istar.dto.UserWrapper;
import com.example.istar.dto.PageModel;
import com.example.istar.dto.LoginModel;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.management.relation.Role;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
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
@RequestMapping("/users")
public class UserController {
    @Resource
    private UserServiceImpl userService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisCache redisCache;


    @PostMapping("/login")
    @ApiOperation(value = "注册/登录", notes = "用户注册,或者登录")
    public R<UserWrapper> preRegister(@RequestBody LoginModel model) throws Exp {
        if (model.isBadFormat()) {
            throw Exp.from(ResultCode.ERROR_PARAM);
        }
        ///1, 校验验证码
        String redisCode = redisCache.getCacheObject(RedisConst.REDIS_CODE_LOGIN + model.getKey());
        if (redisCode != null && redisCode.equals(model.getCode())) {
            ///检验成功，删除验证码
            redisCache.deleteObject(RedisConst.REDIS_CODE_LOGIN + model.getKey());
            User user = null;
            if (RegexTool.isEmail(model.getKey())) {
                user = userService.getOne(new QueryWrapper<User>().eq("email", model.getKey()));
            } else if (RegexTool.isMobiles(model.getKey())) {
                user = userService.getOne(new QueryWrapper<User>().eq("mobile", model.getKey()));
            }
            ///新用户
            if (user == null) {
                user = generateUser(model);
                boolean save = userService.save(user);
                if (!save) {
                    throw Exp.from(ResultCode.REGISTER_ERROR);
                }
            }
            String token = JwtUtil.generateToken(user.getUuid());
            if (token == null) {
                throw Exp.from(ResultCode.FAILED);
            }
            LoginUser loginUser = new LoginUser(user, Arrays.asList(user.getRoles().split(",")));
            redisCache.setCacheObject(RedisConst.REDIS_LOGIN_TOKEN + user.getUuid(), loginUser, JwtUtil.EXPIRE_TIME, JwtUtil.TIME_UNIT);
            return R.ok(new UserWrapper(token, user));
        }
        throw Exp.from(ResultCode.CODE_ERROR);


    }

    private User generateUser(LoginModel model) {
        User user = new User();
        if (RegexTool.isEmail(model.getKey())) {
            user.setEmail(model.getKey());
        } else if (RegexTool.isMobiles(model.getKey())) {
            user.setMobile(model.getKey());
        }
        user.setUuid(UUID.randomUUID().toString(false));
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(System.currentTimeMillis());
        user.setRoles(Roles.SYS_USER);
        user.setStatus(0);
        user.setPoint(0L);
        user.setBalance(0.0);
        return user;
    }

    ///TODO hasRole 默认增加ROLE_前缀
//    @PreAuthorize("hasAuthority('ROLE_ROOT1')")
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("")
    public R<List<User>> getUsers(PageModel pageModel) {
        return R.ok(userService.queryUsers(pageModel));
    }

    @ApiOperation(value = "获取单个用户信息", notes = "获取单个用户信息")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("/{uuid}")
    public R<User> getUser(@PathVariable String uuid) {
        return R.ok(userService.getOne(new QueryWrapper<User>().eq("uuid", uuid)));
    }

    /**
     * 更新用户信息
     *
     * @return R<User>
     */
    @PatchMapping("")
    @ApiOperation(value = "用户更新", notes = "用户更新", response = User.class)
    public R<User> updateUser() {
        return R.ok();
    }

    @DeleteMapping("/{uuid}")
    @ApiOperation(value = "用户销户", notes = "用户销户")
    public R<Boolean> deleteUser(@PathVariable String uuid) throws Exp {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //是自己或者是超级管理员，则可以删除
        if (loginUser.getUser().getUuid().equals(uuid) || loginUser.getUser().getRoles().equals(Roles.SYS_SUPER_ADMIN)) {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("uuid", loginUser.getUser().getUuid());
            User one = userService.getOne(wrapper);
            if (one != null) {
                one.setStatus(-1);
                boolean update = userService.updateById(one);
                redisCache.deleteObject(RedisConst.REDIS_LOGIN_TOKEN + loginUser.getUser().getUuid());
                return update ? R.ok() : R.fail(ResultCode.OPERATION_FAILED);
            }
            return R.fail();

        }
        ///不是抛出异常
        throw Exp.from(ResultCode.OPERATION_FORBIDDEN);
    }


}
