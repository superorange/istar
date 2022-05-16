package com.example.istar.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.example.istar.utils.LocalDateTimeSerializer;
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
 * @since 2022-04-19
 */
@Getter
@Setter
@TableName("t_video")
@ApiModel(value = "Video对象", description = "")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("视频所属用户")
    private String uuid;

    @ApiModelProperty("视频id")
    private String videoId;

    @ApiModelProperty("视频hash")
    private String videoHash;

    @ApiModelProperty("视频url")
    private String videoUrl;

    @ApiModelProperty("视频封面")
    private String videoCover;

    @ApiModelProperty("视频标题")
    private String videoTitle;

    @ApiModelProperty("视频描述")
    private String videoDesc;

    @ApiModelProperty("视频时长")
    private Integer videoDuration;

    @ApiModelProperty("视频大小单位:字节")
    private Integer videoSize;

    @ApiModelProperty("视频状态0正常,-1禁止")
    private Integer videoStatus;

    @ApiModelProperty("视频来源")
    private Integer videoFrom;

    @ApiModelProperty("视频禁止原因")
    private String videoForbiddenReason;

    @ApiModelProperty("视频禁止原因时间")
    private Long videoForbiddenReasonTime;

    @ApiModelProperty("视频价格")
    private Double videoPrice;

    @ApiModelProperty("视频浏览量")
    private Integer videoViews;

    @ApiModelProperty("视频点赞量")
    private Integer videoLikes;

    @ApiModelProperty("视频评论量")
    private Integer videoComments;

    @ApiModelProperty("视频收藏量")
    private Integer videoFavorites;

    @ApiModelProperty("视频购买人数")
    private Integer videoPurchase;

    @ApiModelProperty("视频标签")
    private String videoTags;

    @ApiModelProperty("视频类型")
    private Integer videoType;

    @ApiModelProperty(value = "视频创建时间", dataType = "java.lang.String")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long videoCreateTime;

    @ApiModelProperty(value = "视频更新时间", dataType = "java.lang.String")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long videoUpdateTime;

    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    @ApiModelProperty(value = "视频删除时间", dataType = "java.lang.String")
    private Long videoDeleteTime;


}
