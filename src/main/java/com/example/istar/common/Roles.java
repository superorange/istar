package com.example.istar.common;

import com.example.istar.handler.LoginUser;

import java.util.Arrays;
import java.util.Collections;
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

    //自己可以编辑 -1，0，1
    List<Integer> ownerEdit = Arrays.asList(-1, 0, 1);
    //自己可见 -2，-1，0，1
    List<Integer> ownerSee = Arrays.asList(-2, -1, 0, 1);
    //公开可见 0
    List<Integer> publicSee = Collections.singletonList(0);

    /*
     * 权限类型
     * 自己是否可以编辑
     */
    static boolean hasSuperAdminEdit(Integer status) {
        return status >= 0;
    }

    static boolean hasSelfEdit(Integer status) {
        return ownerEdit.contains(status);
    }

    static boolean hasPublicEdit(Integer status) {
        return publicSee.contains(status);
    }

    static boolean isSuperAdmin() {
        return SYS_SUPER_ADMIN.equals(LoginUser.getCurrentUser().getUserEntity().getRoles());
    }


}
