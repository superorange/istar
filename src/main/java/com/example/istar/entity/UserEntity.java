package com.example.istar.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.istar.utils.serializer.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 *
 * </p>
 *
 * @author tian
 * @since 2022-05-22
 */
@Getter
@Setter
@TableName("t_user")
@ToString
@ApiModel(value = "User对象", description = "")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户识别码")
    private String uuid;

    @ApiModelProperty("性别0女1男")
    private Integer gender;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("签名")
    private String signature;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("生日")
    private LocalDateTime birthday;

    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty("用户状态0正常-1禁用")
    private Integer status;

    @ApiModelProperty("会员积分")
    private Long point;

    @ApiModelProperty(value = "创建时间", dataType = "java.lang.String")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long createTime;

    @ApiModelProperty(value = "修改时间", dataType = "java.lang.String")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long modifyTime;

    @ApiModelProperty("用户余额")
    private Double balance;

    @ApiModelProperty("角色权限--role_sys_superAdmin,role_sys_admin,role_common_user")
    private String roles;


}
