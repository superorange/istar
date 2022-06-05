package com.example.istar.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.istar.dto.UserQueryModel;
import com.example.istar.entity.User;
import com.example.istar.handler.LoginUser;
import com.example.istar.dto.UserWrapper;
import com.example.istar.dto.PageModel;
import com.example.istar.dto.RegisterDTO;
import com.example.istar.model.LoginModel;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
    private RedisCache redisCache;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public R<UserWrapper> register(@RequestBody RegisterDTO dto) throws Exp {
        if (!dto.checkOk()) {
            throw Exp.from(ResultCode.ERROR_PARAM);
        }
        User one = userService.queryUserByUsername(dto.getUsername());
        if (one != null) {
            throw Exp.from(ResultCode.USER_EXIST);
        }
        User user = generateUser(dto);
        boolean save = userService.save(user);
        if (!save) {
            throw Exp.from(ResultCode.FAILED);
        }
        String token = JwtUtil.generateToken(user.getUuid());
        if (ObjectUtils.isEmpty(token)) {
            throw Exp.from(ResultCode.FAILED);
        }
        LoginUser loginUser = new LoginUser(user, Arrays.asList(user.getRoles().split(",")));
        redisCache.setCacheObject(JwtUtil.REDIS_TOKEN_KEY + user.getUuid(), loginUser, JwtUtil.EXPIRE_TIME, JwtUtil.TIME_UNIT);
        return R.ok(new UserWrapper(token, user));

    }

    private User generateUser(RegisterDTO dto) {
        User user = new User();
        user.setUuid(UUID.randomUUID().toString(false));
        user.setUsername(dto.getUsername());
        user.setPassword(MD5.create().digestHex(dto.getPassword()));
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(System.currentTimeMillis());
        user.setRoles("role_common_user");
        return user;
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户登录")
    public R<UserWrapper> login(@RequestBody LoginModel loginModel) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginModel.getUsername(), loginModel.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (ObjectUtils.isEmpty(authenticate)) {
            throw new UsernameNotFoundException(null);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String token = JwtUtil.generateToken(loginUser.getUser().getUuid());
        UserWrapper dto = new UserWrapper(token, loginUser.getUser());
        redisCache.setCacheObject(JwtUtil.REDIS_TOKEN_KEY + loginUser.getUser().getUuid(), loginUser, JwtUtil.EXPIRE_TIME, JwtUtil.TIME_UNIT);
        return R.ok(dto);
    }

    ///TODO hasRole 默认增加ROLE_前缀
    ///@PreAuthorize("hasAuthority('ROLE_ROOT1')")
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("/getUsers")
    public R<PageWrapper<User>> getUsers(PageModel pageModel) {
        PageWrapper<User> userPageWrapper = userService.queryUsers(pageModel, false);
        return R.ok(userPageWrapper);
    }

    @ApiOperation(value = "获取单个用户信息", notes = "获取单个用户信息")
    @PreAuthorize("@userExpression.isSuperAdmin()")
    @GetMapping("/getUser")
    public R<User> generateUser(UserQueryModel model) throws Exp {
        if (model.checkIsNull()) {
            throw Exp.from(ResultCode.ERROR_PARAM);
        }
        User user = userService.queryUser(model);
        return R.ok(user);

    }

    @PatchMapping("/updateUser")
    @ApiOperation(value = "用户更新", notes = "用户更新", response = User.class)
    public R<User> updateUser(@RequestBody RegisterDTO registerDTO) throws Exp {
        if (registerDTO.checkOk()) {
            throw Exp.from("登录失败，请检查输入信息");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user _name", registerDTO.getUsername());
        User one = userService.getOne(wrapper);
        if (one == null) {
            throw Exp.from("登录失败");
        }
        one.setPassword(registerDTO.getPassword());
        userService.saveOrUpdate(one);
        return R.ok(one);

    }

    @DeleteMapping("/deleteUser/{userName}")
    @ApiOperation(value = "用户删除", notes = "用户更新")
    public R<Boolean> deleteUser(@PathVariable String userName) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", userName);
        boolean remove = userService.remove(wrapper);
        return R.ok(remove);
    }

    @ApiOperation(value = "接口转发测试", notes = "接口转发测试")
    @GetMapping("/moveTo")
    public String moveTo(HttpServletResponse response) {
        return "/getUserListByMapper";
    }


}
