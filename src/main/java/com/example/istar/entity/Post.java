package com.example.istar.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;

import com.example.istar.utils.LocalDateTimeSerializer;
import com.example.istar.utils.StatusSerializer;
import com.example.istar.utils.StringToListSerializer;
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
@TableName("t_post")
@ApiModel(value = "Post对象", description = "")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户识别码")
    @JSONField(serialize = false)
    private String uuid;

    @ApiModelProperty("帖子id")
    private String postId;

    @ApiModelProperty(value = "图片ID列表，用分号分隔", dataType = "java.util.List")
    @JSONField(serialize = false)
    private String picId;

    @ApiModelProperty(value = "视频ID列表，用分号分隔", dataType = "java.util.List")
    @JSONField(serialize = false)
    private String videoId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;
    @ApiModelProperty(value = "状态0正常，-1禁用,1删除", dataType = "java.lang.Boolean")
    @JSONField(serializeUsing = StatusSerializer.class, serialize = false)
    private Integer status;

    @ApiModelProperty(value = "创建时间", dataType = "java.lang.String")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long gmtCreate;
    @ApiModelProperty(value = "修改时间", dataType = "java.lang.String")
    @JSONField(serializeUsing = LocalDateTimeSerializer.class)
    private Long gmtModified;

}
