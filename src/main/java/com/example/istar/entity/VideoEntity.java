package com.example.istar.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.example.istar.utils.LocalDateTimeSerializer;
import com.example.istar.utils.StatusSerializer;
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
@TableName("t_video")
@ApiModel(value = "Video对象", description = "")
public class VideoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户识别码,可以为空")
    private String uuid;

    @ApiModelProperty("视频id")
    private String videoId;

    @ApiModelProperty("视频hash")
    private String videoHash;

    @ApiModelProperty("视频地址")
    private String videoUrl;

    @ApiModelProperty("视频名")
    private String videoName;

    @ApiModelProperty("视频大小,单位字节")
    private Long videoSize;

    @ApiModelProperty("视频类型")
    private String videoType;

    @ApiModelProperty(value = "状态0正常，-1禁用,1删除", dataType = "java.lang.Boolean")
    @JSONField(serializeUsing = StatusSerializer.class)
    private Integer status;

    @ApiModelProperty(value = "创建时间", dataType = "java.lang.String")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long createTime;


}
