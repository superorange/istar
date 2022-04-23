package com.example.istar.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.istar.entity.User;
import com.example.istar.model.PageModel;
import com.example.istar.model.RegisterModel;
import com.example.istar.service.impl.UserServiceImpl;
import com.example.istar.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public R<?> register(@RequestBody RegisterModel registerModel) {
        if (!registerModel.isValid()) {
            return R.fail("注册失败，请检查输入信息");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", registerModel.getUserName());
        User one = userService.getOne(wrapper);
        if (one != null) {
            return R.fail("用户名已存在");
        }
        User user = new User();
        user.setUserName(registerModel.getUserName());
        user.setPassword(registerModel.getPassword());
        user.setUuid(UUID.randomUUID().toString());
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(System.currentTimeMillis());
        boolean save = userService.save(user);
        if (save) {
            return R.ok(user);
        }
        return R.fail("注册失败");
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户登录")
    public R<?> login(@RequestBody RegisterModel registerModel) {
        if (!registerModel.isValid()) {
            return R.fail("登录失败，请检查输入信息");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", registerModel.getUserName()).eq("password", registerModel.getPassword());
        User one = userService.getOne(wrapper);
        if (one != null) {
            return R.ok(one);
        }
        return R.fail("登录失败");

    }

    @GetMapping("/getUserList")
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    public R<List<User>> getUsers(PageModel pageModel) {
        List<User> users = userService.queryUserList(pageModel.getLimitIndex(), pageModel.getPageSize());
        return R.ok(users);
    }

    @GetMapping("/getUserListByMapper")
    @ApiOperation(value = "获取用户列表ByMapper", notes = "获取用户列表")
    public R<List<User>> getUsersByMapper(PageModel pageModel) {
        if (pageModel.isLarge()) {
            return R.ok(new ArrayList<>());
        }
        Page<User> userPage = userService.queryUserListByMapper(pageModel.getLimitIndex(), pageModel.getPageSize());
        return R.ok(userPage.getRecords());
    }

    @PostMapping("/updateUser")
    @ApiOperation(value = "用户更新", notes = "用户更新", response = User.class)
    public R<?> updateUser(@RequestBody RegisterModel registerModel) {
        if (!registerModel.isValid()) {
            return R.fail("登录失败，请检查输入信息");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", registerModel.getUserName());
        User one = userService.getOne(wrapper);
        if (one != null) {
            one.setPassword(registerModel.getPassword());
            userService.saveOrUpdate(one);
            return R.ok(one);
        }
        return R.fail("登录失败");
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
