package com.example.istar.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.example.istar.utils.serializer.LocalDateTimeSerializer;
import com.example.istar.utils.serializer.StatusSerializer;
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
@TableName(value = "t_topic", autoResultMap = true)
@ApiModel(value = "Topic对象", description = "")
public class TopicEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户识别码")
    private String uuid;

    @ApiModelProperty("文章id")
    private String topicId;

    @ApiModelProperty("图片ID列表")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    @JSONField(serialize = false)
    private List<String> pictureIdList;

    @ApiModelProperty("视频ID列表")
    @JSONField(serialize = false)
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> videoIdList;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;


    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("状态0正常,-1禁用,1删除")
    @JSONField(serializeUsing = StatusSerializer.class, serialize = false)
    private Integer status;

    @ApiModelProperty("创建时间")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long createTime;

    @ApiModelProperty("创建时间")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long modifyTime;


    ///前端字段

    @ApiModelProperty(value = "视频详细信息")
    @TableField(exist = false)
    private List<VideoEntity> videos;

    @ApiModelProperty(value = "图片详细信息")
    @TableField(exist = false)
    private List<PictureEntity> pictures;

    @ApiModelProperty(value = "头像地址")
    @TableField(exist = false)
    private String avatar;

    @ApiModelProperty(value = "用户昵称")
    @TableField(exist = false)
    private String nickName;

}
