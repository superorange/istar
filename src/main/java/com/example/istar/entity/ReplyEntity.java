package com.example.istar.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.example.istar.utils.serializer.LocalDateTimeSerializer;
import com.example.istar.utils.serializer.StatusSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 回复表
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
@Getter
@Setter
@TableName("t_reply")
@ApiModel(value = "Reply对象", description = "回复表")
public class ReplyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("评论用户ID")
    private String uuid;

    @ApiModelProperty("被回复评论的ID")
    private String commentId;

    @ApiModelProperty("回复评论ID")
    private String replyId;

    @ApiModelProperty("被回复的回复评论ID")
    private String toReplyId;

    @ApiModelProperty("被回复的回复用户ID")
    private String toReplyUuid;

    @ApiModelProperty("回复内容")
    private String content;

    @ApiModelProperty("状态0正常,-1禁用,1删除")
    @JSONField(serializeUsing = StatusSerializer.class, serialize = false)
    private Integer status;

    @ApiModelProperty(value = "创建时间", dataType = "java.lang.String")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long createTime;


    @ApiModelProperty("点赞数")
    private Integer likeCount;


}
