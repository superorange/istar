package com.example.istar.common;

import com.example.istar.handler.LoginUser;
import com.example.istar.utils.Exp;

import java.util.Arrays;
import java.util.List;

public interface Roles {
    String SYS_SUPER_ADMIN = "role_sys_super_admin";
    String SYS_ADMIN = "role_sys_admin";
    String SYS_USER = "role_sys_user";

    //0--正常 默认值 可见
    //负数 代表系统操作，一般是涉及违规
    //
    //-3 不可见 违规被删除，不可修改编辑
    //-2 对自己可见 违规被隐藏，不可修改编辑，可以申诉处理
    //-1 对自己可见 违规被隐藏，可以重新修改编辑

    //正数 代表用户操作，一般是用户自己隐藏或者删除
    //3 不可见 用户删除 ，不可修改编辑
    //2 预留
    //1 对自己可见 用户隐藏 ，可以重新修改编辑


    /**
     * 自己删除
     */
    Integer SELF_DELETE = 3;
    /**
     * 预留
     */
    Integer SELF_FUTURE = 2;

    /**
     * 自己隐藏
     */
    Integer SELF_HINT = 1;
    /**
     * 公开可见
     */
    Integer PUBLIC_SEE = 0;

    /**
     * 管理员隐藏，自己可见
     */
    Integer ADMIN_HINT = -1;
    /**
     * 管理员删除,自己可见，申诉
     */
    Integer ADMIN_DELETE_OWNER_SEE = -2;


    /**
     * 管理员删除,不可见
     */
    Integer ADMIN_DELETE = -3;

    /**
     * 自己可以编辑 0，1,2
     */
    List<Integer> OWNER_EDIT = Arrays.asList(SELF_HINT, PUBLIC_SEE, SELF_FUTURE);
    /**
     * 自己可见  -2，-1，0，1,2
     */
    List<Integer> OWNER_SEE = Arrays.asList(ADMIN_DELETE_OWNER_SEE, ADMIN_HINT, PUBLIC_SEE, SELF_HINT, SELF_FUTURE);


    /**
     * 判断自己是否可以编辑
     *
     * @param status 当前状态
     * @return boolean 是否可以编辑
     */
    static boolean ownerCanEdit(Integer status) {
        return OWNER_EDIT.contains(status);
    }

    /**
     * 判断自己是否可见
     *
     * @param status 当前状态
     * @return 是否可见
     */
    static boolean ownerCanSee(Integer status) {
        return OWNER_SEE.contains(status);
    }

    /**
     * 判断是否公开可见
     *
     * @param status 当前状态
     * @return 是否可见
     */
    static boolean publicCanSee(Integer status) {
        return PUBLIC_SEE.equals(status);
    }

    /**
     * 判断是否是超级管理员
     *
     * @return boolean
     * @throws Exp 抛出异常
     */
    static boolean isSuperAdmin() throws Exp {
        return SYS_SUPER_ADMIN.equals(LoginUser.getCurrentUserAndThrow().getUserEntity().getRoles());
    }


}
