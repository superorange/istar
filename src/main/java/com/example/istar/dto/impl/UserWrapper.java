package com.example.istar.dto.impl;

import com.example.istar.entity.UserEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserWrapper {
    @ApiModelProperty(value = "用户JWT-Token")
    private final String token;
    @ApiModelProperty(value = "用户实体信息")
    private final UserEntity userEntity;
}
