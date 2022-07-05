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
 * 评论表
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
@Getter
@Setter
@TableName("t_topic_comment")
@ApiModel(value = "TopicComment对象", description = "评论表")
public class TopicCommentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("评论用户ID")
    private String uuid;

    @ApiModelProperty("评论主题ID")
    private String topicId;

    @ApiModelProperty("评论ID")
    private String commentId;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("状态0正常,-1禁用,1删除")
    @JSONField(serializeUsing = StatusSerializer.class, serialize = false)
    private Integer status;

    @ApiModelProperty("创建时间")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long createTime;

    @ApiModelProperty("点赞数")
    private Long likeCount;
}
