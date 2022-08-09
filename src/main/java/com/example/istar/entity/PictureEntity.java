package com.example.istar.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.example.istar.utils.serializer.LocalDateTimeSerializer;
import com.example.istar.utils.serializer.MinioPicSerializer;
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
 * @since 2022-07-03
 */
@Getter
@Setter
@TableName("t_picture")
@ApiModel(value = "Picture对象", description = "")
public class PictureEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @JSONField(serialize = false)
    private Long id;

    @ApiModelProperty("用户识别码,可以为空")
    @JSONField(serialize = false)
    private String uuid;

    @ApiModelProperty("图片id")
    private String picId;

    @ApiModelProperty("文件类型")
    private String picType;

    @ApiModelProperty(value = "状态0正常，-1禁用,1删除", dataType = "java.lang.Boolean")
    private Integer status;

    @ApiModelProperty(value = "创建时间", dataType = "java.lang.String")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long createTime;

    //TODO 为什么没有生效，一直返回null
    @ApiModelProperty(value = "图片地址")
    @TableField(exist = false)
    @JSONField(serializeUsing = MinioPicSerializer.class)
    private String picUrl;


}
