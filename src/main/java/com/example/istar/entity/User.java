package com.example.istar.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.istar.configuration.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author tian
 * @since 2022-04-18
 */
@Getter
@Setter
@TableName("t_user")
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户识别码")
    private String uuid;

    @ApiModelProperty("性别0女1男")
    @JSONField(serialize = false)
    private Boolean gender;

    @ApiModelProperty("昵称")
    private String nickName;


    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("生日")
    private LocalDate birthday;

    @ApiModelProperty("头像")
    private String avatarUrl;

    @ApiModelProperty("开放openid")
    private String openid;

    @ApiModelProperty("开放session_key")
    private String sessionKey;

    @ApiModelProperty("用户状态正常")
    private Boolean status;

    @ApiModelProperty("会员积分")
    private Long point;

    @ApiModelProperty("是否删除")
    private Boolean deleted;

    @ApiModelProperty("创建时间")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long gmtCreate;

    @ApiModelProperty("修改时间")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long gmtModified;

    @ApiModelProperty("用户余额")
    private Double balance;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("国家")
    private String country;

    @ApiModelProperty("语言")
    private String language;

    @ApiModelProperty("省份")
    @JSONField(serialize = false)
    private String province;


}
